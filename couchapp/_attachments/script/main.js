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

var getEffectiveDate = function(doc) {
	if ('effective' in doc) { return doc.effective; }
	return dateObjectToArray(new Date());
}

var renderDoc = function(doc) {
	var html = '<div class="doc">';
	html +=  '<div class="thumb">';
	html +=  docHelper.hasThumbnail(doc) ? '<img src="../../'+doc._id+'/'+doc.thumbnail+'"/>' : 'No thumb';
	html +=    '<br><a href="../../'+doc._id+'/raw" target="_blank">Download</a>';
	html +=  '</div>';
	html +=  '<div class="filename"><span class="key">Filename</span><span class="value">'+doc.filename+'</span></div>';
	html +=  '<div><span class="key">Uploaded</span><span class="value">'+dateArrayToString(doc.uploaded)+'</span></div>';
	html +=  '<div><span class="key">Effective</span><span class="value">'+dateArrayToString(getEffectiveDate(doc))+'</span></div>';
	html +=  '<ul>';

	var tags = ('tags' in doc) ? doc.tags : [];
	for (var i = 0; i < tags.length; i++) {
		html += '<li>'+tags[i]+'</li>';
	}
	html += '</ul>';
	html += '</div>';

	var docElement = $(html);
	docElement.click(function(event) {
		if (!$(event.target).is("a")) {
			document.location.href = "document.html#" + doc._id;
		}
	});
	return docElement;
};
