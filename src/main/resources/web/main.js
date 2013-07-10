var docs = null;
var tags = {};

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
	
	var files = $('#files');
	files.empty();
	var newFiles = $('#new > div');
	newFiles.empty();
	for (var id in docs) {
		var doc = docs[id];
		
		var html = '<div class="doc">';
		html += '<div class="thumb">';
		if (doc.thumb) {
			html += '<img src="/fetch?id='+id+'&type=thumb"/>';
		} else {
			html += 'No thumb';
		}
		html += '<br><a href="/fetch?id='+id+'&type=raw" target="_blank">Download</a></div>';
		html += '<div><span class="key">Filename</span><span class="value">'+doc.filename+'</span></div>';
		html += '<div><span class="key">Uploaded</span><span class="value">'+doc.uploaded+'</span></div>';
		html += '<div><span class="key">Effective</span><span class="value">'+doc.effective+'</span></div>';
		html += '<ul>';
		
		var tagsAreSelected = false;
		if (doc.tags.length > 0) {
			for (var i = 0; i < doc.tags.length; i++) {
				if (tags[doc.tags[i]]) { tagsAreSelected = true; }
				html += '<li>'+doc.tags[i]+'</li>';
			}
		} else {
			// Special case for untagged docs
			tagsAreSelected = true;
			for (var tagName in tags) {
				if (tags[tagName]) {
					tagsAreSelected = false;
				}
			}
		}
		html += '</ul>';
		html += '</div>';
		
		var clickListener = function(id){
			return function(event) {
				if (!$(event.target).is("a")) {
					openDoc(id);
				}
			}
		}(id);
		
		if (doc.unseen) {
			var docElement = $(html);
			docElement.click(clickListener);
			newFiles.append(docElement);
		}
		if (tagsAreSelected && !doc.unseen) {
			var docElement = $(html);
			docElement.click(clickListener);
			files.append(docElement);
		}
	}
	if (newFiles.children().size() == 0) {
		$('#new').hide();
	} else {
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
