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

.directive('fcMiniFile', function () {
  return {
    restrict: 'E', // allow as an element; the default is only an attribute
    scope: {       // create an isolate scope
      doc: '='  // map the var in the doc attribute to this scope
    },
    templateUrl: 'partials/fcMiniFile.html', // load the template file
  };
});