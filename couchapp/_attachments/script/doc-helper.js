var docHelper = {
	hasThumbnail : function(doc) {
		return 'thumbnail' in doc && doc.thumbnail !== false;
	},
	isThumbnailDisabled : function(doc) {
		return 'thumbnail' in doc && doc.thumbnail === false;
	},
	needsThumbnail : function(doc) {
		return !('thumbnail' in doc);
	}
};