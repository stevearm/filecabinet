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
        this.viewUrl = function(that) {
            return function(designDocId, viewName) {
                if (!designDocId || !viewName) {
                    return "";
                }
                return "/" + that.currentDb() + "/_design/" + designDocId + "/_view/" + viewName;
            };
        }(this);
    }
])

.service("DateUtils", [
    function() {
        this.toLocalIso8601 = function(date) {
            function pad(num) {
                var norm = Math.abs(Math.floor(num));
                return (norm < 10 ? '0' : '') + norm;
            }

            var tzo = -date.getTimezoneOffset();
            var sign = tzo >= 0 ? '+' : '-';
            return date.getFullYear()
                + '-' + pad(date.getMonth()+1)
                + '-' + pad(date.getDate())
                + 'T' + pad(date.getHours())
                + ':' + pad(date.getMinutes())
                + ':' + pad(date.getSeconds())
                + sign + pad(tzo / 60)
                + ':' + pad(tzo % 60);
        };
    }
]);
