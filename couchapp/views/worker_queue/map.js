function(doc) {
	if (doc.type != "document") { return; }
	if (!("sha1" in doc) || !("thumbnail" in doc)) {
		emit(null, 1);
	}
}
