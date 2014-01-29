"use strict";

angular.module("filecabinet.controllers", [])

.controller("IndexCtrl", [
    "$scope", "$http",
    function($scope, $http) {
        $scope.tags = [];
        $scope.docs = [];

        $http.get("/filecabinet/_design/ui/_view/tags?group=true").success(function(data){
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
            $http.post('/filecabinet/_design/ui/_view/tags?reduce=false&include_docs=true', {'keys':tags})
                .success(function(result){
                    $scope.docs = result.rows.map(function(element){ return element.doc; });
                });
        }, true);
    }
])

.controller("DocumentCtrl", [
    "$scope", "$routeParams", "$http", "$q", "Document",
    function($scope, $routeParams, $http, $q, Document) {
        var docId = $routeParams.docId;
        $scope.doc = Document.get({id: docId });
        $scope.imgSrc = "/filecabinet/" + docId + "/thumb/pdf_view";

        $scope.allTags = [];
        $http.get("/filecabinet/_design/ui/_view/tags?group=true").success(function(data){
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
    "$scope", "$http",
    function($scope, $http) {
        $scope.docs = [];
        $http.get("/filecabinet/_design/ui/_view/human_queue?include_docs=true&limit=10").success(function(data){
            $scope.docs = data.rows.map(function(e){ return e.doc; });
        });
    }
]);
