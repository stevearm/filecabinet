"use strict";

angular.module("filecabinet", [
    "ngRoute",
    "ngResource",
    "ui.bootstrap",
    "filecabinet.controllers",
    "filecabinet.directives",
    "filecabinet.factories"
]).

config(['$routeProvider', function($routeProvider) {
  $routeProvider.when(	'/', 
  					{	templateUrl: 	'partials/index.html',
  						controller: 	'IndexCtrl' });
  $routeProvider.when(	'/doc/:docId',
  					{	templateUrl: 	'partials/document.html',
  						controller: 	'DocumentCtrl' });
  $routeProvider.otherwise({redirectTo: '/'});
}]);
