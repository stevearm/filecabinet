"use strict";

angular.module("filecabinet", [
    "ngRoute",
    "ngResource",
    "ui.bootstrap",
    "ngTagsInput",
    "filecabinet.controllers",
    "filecabinet.directives",
    "filecabinet.factories",
    "filecabinet.services"
]).

config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/', {
        templateUrl:    'partials/index.html',
        controller:     'IndexCtrl'
    });
    $routeProvider.when('/doc/:docId', {
        templateUrl:    'partials/document.html',
        controller:     'DocumentCtrl'
    });
    $routeProvider.when('/unseen', {
        templateUrl:    'partials/unseen.html',
        controller:     'UnseenCtrl'
    });
    $routeProvider.when('/unprocessed', {
        templateUrl:    'partials/unseen.html',
        controller:     'UnprocessedCtrl'
    });
    $routeProvider.otherwise({redirectTo: '/'});
}]);
