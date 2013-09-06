function(doc) {
	if (doc.type != "document") { return; }

	var needed = false;
	if (!("sha1" in doc)) {
		needed = true;
	}
	if (!('thumbnail' in doc)) {
		needed = true;
	}

	if (needed) {
		emit(null, 1);
	}
}
