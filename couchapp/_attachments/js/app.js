"use strict";

angular.module("filecabinet", [
    "ngRoute",
    "filecabinet.controllers",
    "filecabinet.directives"
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
