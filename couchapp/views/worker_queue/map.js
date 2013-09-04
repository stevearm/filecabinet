function(doc) {
	// !code _attachments/script/doc-helper.js
	
	if (doc.type != "document") { return; }

	var needed = false;
	if (!("sha1" in doc)) {
		needed = true;
	}
	if (docHelper.needsThumbnail(doc)) {
		needed = true;
	}

	if (needed) { emit(null, 1); }
}
