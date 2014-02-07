"use strict";

angular.module("filecabinet.controllers", [])

.controller("HeaderCtrl", [
    "$scope", "$http", "CouchService",
    function($scope, $http, CouchService) {
        $http.get(CouchService.viewUrl("human_queue")).success(function(data) {
            if (data.rows.length > 0) {
                $scope.unseen = data.rows[0].value;
            }
        });
        $http.get(CouchService.viewUrl("worker_queue")).success(function(data) {
            if (data.rows.length > 0) {
                $scope.unprocessed = data.rows[0].value;
            }
        });
        $http.get(CouchService.listUrl("duplicates", "sha1") + "?group=true").success(function(data){
            // For each group, subtract 1 from the count (because one in each group isn't a dupicate,
            // it's the original), then sum everything
            $scope.duplicates = data.reduce(function(acc,cur){ return acc + cur.value - 1; }, 0);
        });
    }
])

.controller("ListCtrl", [
    "$scope", "$http", "CouchService",
    function($scope, $http, CouchService) {
        $scope.tags = [];
        $scope.docs = [];
        $scope.newestFirst = true;

        $http.get(CouchService.viewUrl("tags") + "?group=true").success(function(data){
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
            $http.post(CouchService.viewUrl("tags") + "?reduce=false&include_docs=true", {'keys':tags})
                .success(function(result){
                    var docMap = {};
                    result.rows.forEach(function(e){
                        docMap[e.id] = e.doc;
                    });
                    var docs = [];
                    for (var id in docMap) {
                        docs.push(docMap[id]);
                    }
                    $scope.docs = docs;
                });
        }, true);
    }
])

.controller("DocumentCtrl", [
    "$scope", "$routeParams", "$http", "$q", "Document", "CouchService",
    function($scope, $routeParams, $http, $q, Document, CouchService) {
        var docId = $routeParams.docId;
        $scope.doc = Document.get({id: docId });

        $scope.attachmentUrl = CouchService.attachmentUrl;

        // allTags is just used inside the deferred loadTags. Should clean this up
        $scope.allTags = [];
        $http.get(CouchService.viewUrl("tags") + "?group=true").success(function(data){
            $scope.allTags = data.rows
                                .map(function(element) { return element.key; })
                                .filter(function(e) { return e != null; });
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
        $http.get(CouchService.viewUrl("human_queue") + "?include_docs=true&limit=10&reduce=false").success(function(data){
            $scope.docs = data.rows.map(function(e){ return e.doc; });
        });
    }
])

.controller("UnprocessedCtrl", [
    "$scope", "$http", "CouchService",
    function($scope, $http, CouchService) {
        $scope.queueName = "unprocessed";
        $scope.docs = [];
        $http.get(CouchService.viewUrl("worker_queue") + "?include_docs=true&limit=10&reduce=false").success(function(data){
            $scope.docs = data.rows.map(function(e){ return e.doc; });
        });
    }
])

.controller("DuplicatesCtrl", [
    "$scope", "$http", "$routeParams", "CouchService",
    function($scope, $http, $routeParams, CouchService) {
        var currentHash = $routeParams.hash;
        $scope.hashes = [];
        $http.get(CouchService.listUrl("duplicates", "sha1") + "?group=true").success(function(data){
            data.forEach(function(e){
                var hash = { sha1: e.key, count: e.value, docs: [] };
                $scope.hashes.push(hash);

                // If this is the hash we're looking at, load all docs for it
                if (hash.sha1 == currentHash) {
                    $http.get(CouchService.viewUrl("sha1") + "?reduce=false&key=\"" + currentHash + "\"&include_docs=true")
                        .success(function(hash) { return function(data){
                            hash.docs = data.rows.map(function(e) { return e.doc; });
                        }}(hash));
                }
            });
        });
    }
])

.controller("UploadCtrl", [
    "$scope", "$http", "$upload", "CouchService", "DateUtils",
    function($scope, $http, $upload, CouchService, DateUtils) {
        $scope.uploads = [];

        $scope.onFileSelect = function($files) {
            $files.forEach(function(file) {
                var upload = {
                    selectedFile: file,
                    start: new Date(),
                    filename: file.name.replace(/[^0-9A-Za-z-_.()]/g, "_")
                };
                $scope.uploads.push(upload);
                createDocument(upload);
            });
        };

        var createDocument = function(upload) {
            upload.status = "Creating db record";
            $http({
                method: "POST",
                url: "/" + CouchService.currentDb() + "/",
                data: {
                    type: "document",
                    unseen: true,
                    uploaded: DateUtils.toLocalIso8601(new Date()),
                    raw: upload.filename,
                    tags: []
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
            upload.status = "Uploading file";
            var fileReader = new FileReader();
            fileReader.readAsArrayBuffer(upload.selectedFile);
            fileReader.onload = function(e) {
                upload.attachmentUpload = $upload.http({
                    url: CouchService.attachmentUrl(upload.id, upload.filename) + '?rev=' + upload.rev,
                    headers: {'Content-Type': upload.selectedFile.type},
                    data: e.target.result,
                    method: 'PUT'
                }).then(function(response) {
                    if (response.data.ok) {
                        upload.status = "Finished uploading";
                        upload.rev = response.rev;
                        upload.time = new Date() - upload.start;
                        delete upload.attachmentUpload;
                    } else {
                        console.log("Got upload response", response);
                    }
                }, null, function(evt) {
                    // Math.min is to fix IE which reports 200% sometimes
                    upload.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
                    upload.time = new Date() - upload.start;
                });
            };
        };
    }
]);
