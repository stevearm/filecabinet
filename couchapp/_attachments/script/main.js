var design, db;
$(function() {
	var path = unescape(document.location.pathname).split('/');
	design = path[3];
	db = $.couch.db(path[1]);
	if (window.bootstrap) { window.bootstrap(); }
});

var dateArrayToString = function(dateArray) {
	var dateString = "" + dateArray[0] + "-";
	if (dateArray[1] < 10) {
		dateString += "0";
	}
	dateString += dateArray[1] + "-";
	if (dateArray[2] < 10) {
		dateString += "0";
	}
	dateString += dateArray[2];
	return dateString;
};

var dateStringToArray = function(dateString) {
	var parts = dateString.split("-");
	return [ parseInt(parts[0]), parseInt(parts[1]), parseInt(parts[2]) ];
};

var dateObjectToArray = function(date) {
	return [ 1900 + date.getYear(), date.getMonth() + 1, date.getDate() ];
};