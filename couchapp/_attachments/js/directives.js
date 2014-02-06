"use strict";

angular.module("filecabinet.directives", [])

.directive('draggable', function($document) {
    return function(scope, element, attr) {
        var startX = 0, startY = 0, x = 0, y = 0;
        element.css({
            position: 'relative',
            border: '1px solid red',
            backgroundColor: 'lightgrey',
            cursor: 'pointer'
        });
        element.on('mousedown', function(event) {
            // Prevent default dragging of selected content
            event.preventDefault();
            startX = event.screenX - x;
            startY = event.screenY - y;
            $document.on('mousemove', mousemove);
            $document.on('mouseup', mouseup);
        });

        function mousemove(event) {
            y = event.screenY - startY;
            if (y > 0) { y = 0; }

            x = event.screenX - startX;
            if (x > 0) { x = 0; }

            element.css({
                top: y + 'px',
                left:  x + 'px'
            });
        }

        function mouseup() {
            $document.unbind('mousemove', mousemove);
            $document.unbind('mouseup', mouseup);
        }
    }
})

.directive('docCard', [
    "CouchService",
    function (CouchService) {
        return {
            restrict: 'E', // allow as an element; the default is only an attribute
            scope: {       // create an isolate scope
                doc: '='  // map the var in the doc attribute to this scope
            },
            templateUrl: 'partials/docCard.html', // load the template file
            controller: function($scope){
                $scope.thumbUrl = function() {
                    if ($scope.doc.thumbnail) {
                        return "/" + CouchService.currentDb() + "/" + $scope.doc._id + "/" + $scope.doc.thumbnail;
                    }
                    return null;
                };
            }
        };
    }
])

.directive('activeTab', function ($location) {
    /* from http://stackoverflow.com/a/17496112/28038 */
    return {
        link: function postLink(scope, element, attrs) {
            scope.$on("$routeChangeSuccess", function (event, current, previous) {
                // designed for full re-usability at any path, any level, by using data from attrs
                // declare like this: <li class="nav_tab"><a href="#/home" active-tab="1">HOME</a></li>

                // this var grabs the tab-level off the attribute, or defaults to 1
                var pathLevel = attrs.activeTab || 1,
                // this var finds what the path is at the level specified
                pathToCheck = $location.path().split('/')[pathLevel],
                // this var finds grabs the same level of the href attribute
                tabLink = attrs.href.split('/')[pathLevel];
                // now compare the two:
                if (pathToCheck === tabLink) {
                  element.parent().addClass("active");
                }
                else {
                  element.parent().removeClass("active");
                }
            });
        }
    };
});
