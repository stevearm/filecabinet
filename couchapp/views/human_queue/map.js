function(doc) {
	if (doc.type != "document") { return; }
	if (!doc.seen) {
		emit(null, 1);
	}
}
