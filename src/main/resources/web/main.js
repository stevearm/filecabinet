var docs = null;
var tags = null;
var activeTags = {};

var redraw = function() {
	var tagFilter = $('#tag-filter');
	tagFilter.empty();
	tags.sort();
	for (var i = 0; i < tags.length; i++) {
		var element = $('<span class="tag'+( (activeTags[tags[i]]) ? ' selected' : '' )+'">'+tags[i]+'</span>');
		element.click(function(event) {
			var tmp = $(this);
			var tagName = tmp.html();
			if (tmp.hasClass('selected')) {
				tmp.removeClass('selected');
				activeTags[tagName] = false;
			} else {
				tmp.addClass('selected');
				activeTags[tagName] = true;
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
		var html = '<div class="doc" onclick="openDoc(\''+id+'\')">';
		if (doc.thumb) {
			html += '<img class="thumb" src="/fetch?id='+id+'&type=thumb"/>';
		} else {
			html += 'No thumb';
		}
		html += '<div><span class="key">Filename</span><span class="value">'+doc.filename+'</span></div>';
		html += '<div><span class="key">Uploaded</span><span class="value">'+doc.uploaded+'</span></div>';
		html += '<div><span class="key">Effective</span><span class="value">'+doc.effective+'</span></div>';
		html += '<ul>';
		
		var tagsAreSelected = false;
		for (var i = 0; i < doc.tags.length; i++) {
			if (activeTags[doc.tags[i]]) { tagsAreSelected = true; }
			html += '<li>'+doc.tags[i]+'</li>';
		}
		html += '</ul>';
		html += '</div>';
		if (doc.unseen) {
			newFiles.append(html);
		}
		if (tagsAreSelected) {
			files.append(html);
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
	tagsInput.tagit();
	tagsInput.change(function(){
		doc.tags = tagsInput.val().split(',');
		saveDoc(doc);
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
			unseed:doc.unseen,
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
			tags = json.tags;
			$('#all-files-path').html("All files: "+json.paths.cabinet);
			$('#new-files-path').html("New files: "+json.paths.desk);
			var all = function() {
				for (var i = 0; i < tags.length; i++) {
					activeTags[tags[i]] = true;
				}
				redraw();
			};
			$('#tag-filter-all').click(all);
			$('#tag-filter-none').click(function(){
				for (var i = 0; i < tags.length; i++) {
					activeTags[tags[i]] = false;
				}
				redraw();
			});
			all();
		},
		error : genericAjaxError
	});
	
	$(document).keydown(function(e){
		if(e.keyCode == 27){
			closeDoc();
		}
	});
});
