"use strict";

angular.module("filecabinet.factories", [])

.factory("Document", function($resource, $http) {
    var db = "/filecabinet/"
    var Document = $resource(db + ":id", { id:"@_id", rev:"@_rev"}, {
        delete: {
            method: "DELETE",
            url: db + ":id?rev=:rev"
        }
    });

    Document.prototype.$save = function() {

        // Scrub any objects into strings to prepare for save
        if (this.effective instanceof Date) {
            this.effective = this.effective.toISOString();
        }

        var config = { data: this, method: "POST", url: db };
        if ("_id" in this) {
            config.method = "PUT";
            config.url += this._id;
        }
        $http(config).error(function(data, status, headers, config) {
            console.log("Error saving", data, status, headers, config);
            window.alert("Error saving: " + data.reason);
        }).success(function(original_object){ return function(data, status, headers, config) {
            original_object._rev = data.rev;
        };}(this));
    };
    return Document;
});
