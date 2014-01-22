function(doc) {
	if (doc.type != "document") { return; }
	if (doc.tags) {
		if (doc.tags.length == 0) {
			emit(null, 1);
		} else {
			for (var i = 0; i < doc.tags.length; i++) {
				emit(doc.tags[i], 1);
			}
		}
	}
}