function(newDoc, oldDoc, userCtx) {
	// Don't validate a deletion request
	if (newDoc._deleted) { return; }

	// Helper functions
	var fail = function(message) { throw({"forbidden":message}); };
	
	if (!("type" in newDoc)) { fail("Must have a type"); }
	switch (newDoc.type) {
		case "document":
			if (!oldDoc && !newDoc.unseen) {
				fail("All new documents must be unseen");
			}
			if (!("uploaded" in newDoc)) {
				fail("All documents must have an upload time");
			}
			if (!Array.isArray(newDoc.tags)) {
				fail("tags must be an array");
			}
			break;
		case "prefs":
			if (newDoc._id != "prefs") {
				fail("Only one prefs should exist: _id=prefs");
			}
			break;
		case "worker":
			if (newDoc._id != "worker") {
				fail("Only one worker should exist: _id=worker");
			}
			break;
		default:
			fail("Unsupported document type: "+newDoc.type);
	}
}