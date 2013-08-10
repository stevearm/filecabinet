function(doc) {
	if (doc.unseen) {
		emit(null, 1);
	}
}