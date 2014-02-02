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
]);
