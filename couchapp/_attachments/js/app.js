"use strict";

angular.module("filecabinet", [
    "ngRoute",
    "ngResource",
    "ui.bootstrap",
    "ngTagsInput",
    "angularFileUpload",
    "filecabinet.controllers",
    "filecabinet.directives",
    "filecabinet.factories",
    "filecabinet.services"
])

.config([
    "$routeProvider",
    function($routeProvider) {
        $routeProvider
        .when('/', {
            templateUrl:    'partials/list.html',
            controller:     'ListCtrl'
        })
        .when('/doc/:docId', {
            templateUrl:    'partials/document.html',
            controller:     'DocumentCtrl'
        })
        .when('/unseen', {
            templateUrl:    'partials/queue.html',
            controller:     'UnseenCtrl'
        })
        .when('/unprocessed', {
            templateUrl:    'partials/queue.html',
            controller:     'UnprocessedCtrl'
        })
        .when('/duplicates', {
            templateUrl:    'partials/duplicates.html',
            controller:     'DuplicatesCtrl'
        })
        .when('/duplicates/:hash', {
            templateUrl:    'partials/duplicates.html',
            controller:     'DuplicatesCtrl'
        })
        .when('/upload', {
            templateUrl:    'partials/upload.html',
            controller:     'UploadCtrl'
        })
        .otherwise({redirectTo: '/'});
    }
]);
