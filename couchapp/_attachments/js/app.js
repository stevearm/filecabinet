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
            templateUrl:    'partials/index.html',
            controller:     'IndexCtrl'
        })
        .when('/doc/:docId', {
            templateUrl:    'partials/document.html',
            controller:     'DocumentCtrl'
        })
        .when('/unseen', {
            templateUrl:    'partials/unseen.html',
            controller:     'UnseenCtrl'
        })
        .when('/unprocessed', {
            templateUrl:    'partials/unseen.html',
            controller:     'UnprocessedCtrl'
        })
        .when('/upload', {
            templateUrl:    'partials/upload.html',
            controller:     'UploadCtrl'
        })
        .otherwise({redirectTo: '/'});
    }
]);
