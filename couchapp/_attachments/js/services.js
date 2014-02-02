"use strict";

angular.module("filecabinet.services", [])

.service("CouchService", [
    function() {
        this.currentDb = function() {
            return document.location.pathname.split("/")[1];
        };
        this.attachmentUrl = function(that) {
            return function(docId, attachmentName) {
                if (!docId || !attachmentName) {
                    return "";
                }
                return "/" + that.currentDb() + "/" + docId + "/" + attachmentName;
            };
        }(this);
    }
]);
