var docs = null;
var tags = {};
var dates = {
	from : null,
	fromEnabled : false,
	to : null,
	toEnabled : false,
	newestFirst : true
};
var docIdsDisplayed = [];

var renderDoc = function(doc, cache) {
	if (cache[doc.id] == null) {
		var render = {
			clickListener : function(id){
				return function(event) {
					if (!$(event.target).is("a")) {
						openDoc(id);
					}
				}
			}(doc.id),
			html : ""
		};
		render.html = '<div class="doc">';
		render.html +=  '<div class="thumb">';
		render.html +=    doc.thumb ? '<img src="/fetch?id='+doc.id+'&type=thumb"/>' : 'No thumb';
		render.html +=    '<br><a href="/fetch?id='+doc.id+'&type=raw" target="_blank">Download</a>';
		render.html +=  '</div>';
		render.html +=  '<div class="filename"><span class="key">Filename</span><span class="value">'+doc.filename+'</span></div>';
		render.html +=  '<div><span class="key">Uploaded</span><span class="value">'+doc.uploaded+'</span></div>';
		render.html +=  '<div><span class="key">Effective</span><span class="value">'+doc.effective+'</span></div>';
		render.html +=  '<ul>';

		for (var i = 0; i < doc.tags.length; i++) {
			render.html += '<li>'+doc.tags[i]+'</li>';
		}
		render.html += '</ul>';
		render.html += '</div>';

		cache[doc.id] = render;
	}
	return cache[doc.id];
};

var redraw = function() {
	var tagFilter = $('#tag-filter');
	tagFilter.empty();
	var tagNames = Object.keys(tags);
	tagNames.sort();
	for (var i = 0; i < tagNames.length; i++) {
		var element = $('<span class="tag'+( (tags[tagNames[i]]) ? ' selected' : '' )+'">'+tagNames[i]+'</span>');
		element.click(function(event) {
			var tmp = $(this);
			var tagName = tmp.html();
			if (tmp.hasClass('selected')) {
				tmp.removeClass('selected');
				tags[tagName] = false;
			} else {
				tmp.addClass('selected');
				tags[tagName] = true;
			}
			redraw();
		});
		tagFilter.append(element);
	}
	
	// Filter docs for display
	var allFiles = [];
	var newFiles = [];
	for (var id in docs) {
		var doc = docs[id];

		// Filter out by tags
		var showInAll = false;
		if (!doc.unseen) {
			for (var i = 0; i < doc.tags.length; i++) {
				if (tags[doc.tags[i]]) {
					showInAll = true;
					break;
				}
			}
			if (doc.tags.length == 0) {
				// Special case for untagged docs
				showInAll = true;
				for (var tagName in tags) {
					if (tags[tagName]) {
						showInAll = false;
						break;
					}
				}
			}
		}
		
		// Filter by date
		if (showInAll && dates.fromEnabled) {
			if (doc.effective < dates.from) {
				showInAll = false;
			}
		}
		if (showInAll && dates.toEnabled) {
			if (dates.to < doc.effective) {
				showInAll = false;
			}
		}

		if (showInAll) {
			allFiles.push(doc);
		}

		if (doc.unseen) {
			newFiles.push(doc);
		}
	}

	// Sort allFiles and newFiles
	var comparator = function(a,b) {
		if (a.effective < b.effective) return dates.newestFirst ? 1 : -1;
		if (a.effective > b.effective) return dates.newestFirst ? -1 : 1;
		return 0;
	};
	allFiles.sort(comparator);
	newFiles.sort(comparator);

	// Cache rendering
	var renders = {};

	// Render all files
	docIdsDisplayed = [];
	var allFilesNode = $('#files');
	allFilesNode.empty();
	for (var i = 0; i < allFiles.length; i++) {
		var doc = allFiles[i];
		docIdsDisplayed.push(doc.id);
		var render = renderDoc(doc, renders);
		var docElement = $(render.html);
		docElement.click(render.clickListener);
		allFilesNode.append(docElement);
	}

	// Render new files
	var newFilesNode = $('#new > div');
	newFilesNode.empty();
	if (newFiles.length == 0) {
		$('#new').hide();
	} else {
		for (var i = 0; i < newFiles.length; i++) {
			var doc = newFiles[i];
			var render = renderDoc(doc, renders);
			var docElement = $(render.html);
			docElement.click(render.clickListener);
			newFilesNode.append(docElement);
		}
		$('#new').show();
	}
};

var openDoc = function(id) {
	var doc = docs[id];
	var lightNode = $('#light');
	lightNode.empty();

	var html = '<div id="full-image-viewport">';
	if (doc.thumb) {
		html += '<img id="full-image" src="/fetch?id='+id+'&type=thumb"/>';
	} else {
		html += '<button onclick="generateThumb(\''+id+'\');">Generate</button>'
	}
	html += '</div>';
	html += '<div><a href="/fetch?id='+id+'&type=raw" target="_blank">Download file</a></div>';
	html += '<div><span class="key">Filename</span><span class="value">'+doc.filename+'</span></div>';
	html += '<div><span class="key">Uploaded</span><span class="value">'+doc.uploaded+'</span></div>';
	html += '<div><span class="key">Effective</span><span class="value"><input type="text" id="effective-date" size="20" value="'+doc.effective+'"/></span></div>';
	if (doc.unseen) {
		html += '<div id="unseen"><span class="key">Unseen</span><span class="value"><button>Mark as seen</button></span></div>';
	}
	html += '<input name="tags" id="tags" style="position:relative" value="'+doc.tags.join(',')+'"/>';
	lightNode.append(html);

	if (doc.thumb) {
		$('#full-image').draggable({
			drag: function(event, ui) {
				if(ui.position.top>0) { ui.position.top = 0; }
				var maxtop = ui.helper.parent().height()-ui.helper.height();
				if(ui.position.top<maxtop) { ui.position.top = maxtop; }
				if(ui.position.left>0) { ui.position.left = 0; }
				var maxleft = ui.helper.parent().width()-ui.helper.width();
				if(ui.position.left<maxleft) { ui.position.left = maxleft; }
			}
		});
	}

	var effectiveDate = $('#effective-date');
	effectiveDate.datepicker({
		dateFormat: "yy-mm-dd"
	});
	effectiveDate.change(function(){
		doc.effective = effectiveDate.val();
		saveDoc(doc);
	});

	$('#unseen > span.value > button').click(function(){
		doc.unseen = false;
		saveDoc(doc);
		$('#unseen').remove();
	});

	var tagsInput = $('#tags');
	tagsInput.tagit({
		autocomplete: {
			source: function(request, callback) {
				var suggestions = [];
				for (var tag in tags) {
					if (tag.indexOf(request.term) != -1) {
						suggestions.push(tag);
					}
				}
				callback(suggestions);
			}
		}
	});
	tagsInput.change(function(){
		doc.tags = tagsInput.val().split(',');
		saveDoc(doc);
		for (var i = 0; i < doc.tags.length; i++) {
			if (!tags.hasOwnProperty(doc.tags[i])) {
				tags[doc.tags[i]] = false;
			}
		}
	});

	lightNode.show();
	$('#fade').show();
};

var closeDoc = function() {
	$('#light').hide();
	$('#fade').hide();
};

var saveDoc = function(doc) {
	$.ajax({
		type:"POST",
		traditional:true,
		url:"/cabinet",
		data:{
			id:doc.id,
			action:"saveDoc",
			unseen:doc.unseen,
			tags:doc.tags,
			effective:doc.effective
		},
		success:function(){
			redraw();
		},
		error : genericAjaxError,
		dataType:"json"
	});
};

var generateThumb = function(id) {
	$.ajax({
		type:"POST",
		traditional:true,
		url:"/cabinet",
		data:{
			id:id,
			action:"createThumbnail"
		},
		success:function(){
			docs[id].thumb = true;
			redraw();
			openDoc(id);
		},
		error : genericAjaxError,
		dataType:"json"
	});
}

var genericAjaxError = function(req, response) {
	alert("Error contacting server");
	console.log("Failed request " + response, req);
};

var renderDate = function(date) {
	var month = "" + (date.getMonth() + 1);
	if (month.length == 1) {
		month = "0" + month;
	}
	var day = "" + date.getDate();
	if (day.length == 1) {
		day = "0" + day;
	}
	return (1900 + date.getYear()) + "-" + month + "-" + day;
};

$(document).ready(function() {
	$.ajax({
		url : "/cabinet",
		dataType : "json",
		success : function(json) {
			docs = json.docs;
			for (var i = 0; i < json.tags.length; i++) {
				if (!tags.hasOwnProperty(json.tags[i])) {
					tags[json.tags[i]] = false;
				}
			}
			$('#all-files-path').html("All files: "+json.paths.cabinet);
			$('#new-files-path').html("New files: "+json.paths.desk);
			$('#tag-filter-all').click(function() {
				for (var tag in tags) {
					tags[tag] = true;
				}
				redraw();
			});
			$('#tag-filter-none').click(function(){
				for (var tag in tags) {
					tags[tag] = false;
				}
				redraw();
			});

			// Date from
			var dateElement = $('#tag-filter-from');
			dateElement.click(function(element) {
				return function() {
					if ($(event.target).is("input")) { return; }
					if (dates.fromEnabled) {
						dates.fromEnabled = false;
						element.removeClass('selected');
					} else {
						dates.fromEnabled = true;
						element.addClass('selected');
					}
					redraw();
				};
			}(dateElement));
			dateElement = $('#tag-filter-from input');
			var date = new Date();
			date.setDate(date.getDate() - 30);
			dateElement.val(renderDate(date));
			dateElement.datepicker({ dateFormat: "yy-mm-dd" });
			dateElement.change(function(){
				var element = $(this);
				dates.from = element.val();
				if (dates.fromEnabled && dates.toEnabled && dates.from > dates.to) {
					element.click();
				}
				redraw();
			});

			// Date to
			dateElement = $('#tag-filter-to');
			dateElement.click(function(element) {
				return function() {
					if ($(event.target).is("input")) { return; }
					if (dates.toEnabled) {
						dates.toEnabled = false;
						element.removeClass('selected');
					} else {
						dates.toEnabled = true;
						element.addClass('selected');
					}
					redraw();
				};
			}(dateElement));
			dateElement = $('#tag-filter-to > input');
			dateElement.val(renderDate(new Date()));
			dateElement.datepicker({ dateFormat: "yy-mm-dd" });
			dateElement.change(function(){
				var element = $(this);
				dates.to = element.val();
				if (dates.fromEnabled && dates.toEnabled && dates.from > dates.to) {
					element.click();
				}
				redraw();
			});

			// Sort
			dateElement = $('#tag-sort-order');
			dateElement.click(function(element) {
				return function() {
					if (dates.newestFirst) {
						dates.newestFirst = false;
						element.html('Oldest first');
					} else {
						dates.newestFirst = true;
						element.html('Newest first');
					}
					redraw();
				};
			}(dateElement));

			// Download
			$('#archive-download-link').click(function() {
				if ($(event.target).is("input")) { return; }
				var form = $('#archive-download-form');
				form.find('input[name="pattern"]').val($('#archive-download-link > input').val());
				form.find('input[name="ids"]').val(docIdsDisplayed);
				form.submit();
			});

			redraw();
		},
		error : genericAjaxError
	});

	$(document).keydown(function(e){
		if(e.keyCode == 27){
			closeDoc();
		}
	});
});
