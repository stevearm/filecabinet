var docs = null;
var tags = null;

var refresh = function() {
	var files = $('#files');
	var newFiles = $('#new > div');
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
		for (var i = 0; i < doc.tags.length; i++) {
			html += '<li>'+doc.tags[i]+'</li>';
		}
		html += '</ul>';
		html += '</div>';
		if (doc.unseen) {
			newFiles.append(html);
		}
		files.append(html);
	}
	
	openDoc('b987b2cb274a381637139acbd87fff0c6cf33853');
};

var openDoc = function(id) {
	var doc = docs[id];
	var lightNode = $('#light');
	lightNode.empty();
	
	var html = '<div id="full-image-viewport">';
	html += '<img id="full-image" src="/fetch?id='+id+'&type=thumb"/>';
	html += '</div>';
	html += '<div><span class="key">Filename</span><span class="value">'+doc.filename+'</span></div>';
	html += '<div><span class="key">Uploaded</span><span class="value">'+doc.uploaded+'</span></div>';
	html += '<div><span class="key">Effective</span><span class="value">'+doc.effective+'</span></div>';
	lightNode.append(html);
	
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
	
	var tagsInput = $('<input name="tags" id="tags" style="position:relative" value="'+doc.tags.join(',')+'"/>');
	lightNode.append(tagsInput);
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
			tags:doc.tags
		},
		success:function(){},
		error : genericAjaxError,
		dataType:"json"
	});
};

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
			refresh();
		},
		error : genericAjaxError
	});
	
	$(document).keydown(function(e){
		if(e.keyCode == 27){
			closeDoc();
		}
	});
});
