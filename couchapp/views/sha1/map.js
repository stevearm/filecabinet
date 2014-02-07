function(doc) {
	if (doc.type != "document") { return; }
	if (doc.sha1) {
		emit(doc.sha1, 1);
	}
}
