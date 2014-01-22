function(doc) {
	if (doc.type != "document") { return; }
	if (!doc.processed) {
		emit(null, 1);
	}
}
