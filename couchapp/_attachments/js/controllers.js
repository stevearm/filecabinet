"use strict";

angular.module("filecabinet.controllers", [])

.controller("IndexCtrl", [
    "$scope", "$http", "CouchService",
    function($scope, $http, CouchService) {
        $scope.tags = [];
        $scope.docs = [];

        $http.get("/" + CouchService.currentDb() + "/_design/ui/_view/tags?group=true").success(function(data){
            $scope.tags = data.rows.map(function(element){
                return { name: element.key, show: false };
            });
        });

        $scope.showAllTags = function(show) {
            $scope.tags.forEach(function(element){ element.show = show; });
        }

        $scope.$watch('tags', function(){
            var tags = $scope.tags.filter(function(element){ return element.show; }).map(function(element){ return element.name; });
            if (tags.length == 0) {
                $scope.docs = [];
                return;
            }
            $http.post("/" + CouchService.currentDb() + "/_design/ui/_view/tags?reduce=false&include_docs=true", {'keys':tags})
                .success(function(result){
                    $scope.docs = result.rows.map(function(element){ return element.doc; });
                });
        }, true);
    }
])

.controller("DocumentCtrl", [
    "$scope", "$routeParams", "$http", "$q", "Document", "CouchService",
    function($scope, $routeParams, $http, $q, Document, CouchService) {
        var docId = $routeParams.docId;
        $scope.doc = Document.get({id: docId });

        $scope.attachmentUrl = function(docId, attachmentName) {
            if (!docId || !attachmentName) {
                return "";
            }
            return "/" + CouchService.currentDb() + "/" + docId + "/" + attachmentName;
        }

        // allTags is just used inside the deferred loadTags. Should clean this up
        $scope.allTags = [];
        $http.get("/" + CouchService.currentDb() + "/_design/ui/_view/tags?group=true").success(function(data){
            $scope.allTags = data.rows.map(function(element){ return element.key; });
        });
        $scope.loadTags = function(input) {
            var relevantTags = $scope.allTags.filter(function(e){ return e.indexOf(input) != -1; });

            var deferred = $q.defer();
            deferred.resolve(relevantTags);
            return deferred.promise;
        };
    }
])

.controller("UnseenCtrl", [
    "$scope", "$http", "CouchService",
    function($scope, $http, CouchService) {
        $scope.queueName = "unseen";
        $scope.docs = [];
        $http.get("/" + CouchService.currentDb() + "/_design/ui/_view/human_queue?include_docs=true&limit=10").success(function(data){
            $scope.docs = data.rows.map(function(e){ return e.doc; });
        });
    }
])

.controller("UnprocessedCtrl", [
    "$scope", "$http", "CouchService",
    function($scope, $http, CouchService) {
        $scope.queueName = "unprocessed";
        $scope.docs = [];
        $http.get("/" + CouchService.currentDb() + "/_design/ui/_view/worker_queue?include_docs=true&limit=10").success(function(data){
            $scope.docs = data.rows.map(function(e){ return e.doc; });
        });
    }
])

.controller("UploadCtrl", [
    "$scope", "$http", "$upload",
    function($scope, $http, $upload) {
        $scope.uploads = [];

        $scope.onFileSelect = function($files) {
            $files.forEach(function(file) {
                var upload = {
                    selectedFile: file,
                    filename: file.name.replace(/[^0-9A-Za-z-_()]/g, "_")
                };
                $scope.uploads.push(upload);
                createDocument(upload);
            });
        };

        var getLocalISO8601 = function(date) {
            function pad(num) {
                var norm = Math.abs(Math.floor(num));
                return (norm < 10 ? '0' : '') + norm;
            }

            var tzo = -date.getTimezoneOffset();
            var sign = tzo >= 0 ? '+' : '-';
            return date.getFullYear() 
                + '-' + pad(date.getMonth()+1)
                + '-' + pad(date.getDate())
                + 'T' + pad(date.getHours())
                + ':' + pad(date.getMinutes()) 
                + ':' + pad(date.getSeconds()) 
                + sign + pad(tzo / 60) 
                + ':' + pad(tzo % 60);
        };

        var createDocument = function(upload) {
            upload.status = "Creating db record";
            $http({
                method: "POST",
                url: "/filecabinet/",
                data: {
                    type: "document",
                    unseen: true,
                    uploaded: getLocalISO8601(new Date()),
                    raw: upload.filename
                }
            }).error(function(data, status, headers, config) {
                upload.status = "Error creating document";
                console.log("Error creating document", data, status, headers, config);
            }).success(function(data, status, headers, config) {
                upload.status = "Document created";
                upload.id = data.id;
                upload.rev = data.rev;
                uploadAttachment(upload);
            });
        };

        var uploadAttachment = function(upload) {
            upload.status = "Uploading " + upload.filename;
            var fileReader = new FileReader();
            fileReader.readAsArrayBuffer(upload.selectedFile);
            fileReader.onload = function(e) {
                upload.attachmentUpload = $upload.http({
                    url: '/filecabinet/' + upload.id + '/' + upload.filename + '?rev=' + upload.rev,
                    headers: {'Content-Type': upload.selectedFile.type},
                    data: e.target.result,
                    method: 'PUT'
                }).then(function(response) {
                    if (response.data.ok) {
                        upload.status = "Finished uploading";
                        upload.rev = response.rev;
                        delete upload.attachmentUpload;
                    } else {
                        console.log("Got upload response", response);
                    }
                }, null, function(evt) {
                    // Math.min is to fix IE which reports 200% sometimes
                    upload.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
                });
            };
        };
    }
]);
