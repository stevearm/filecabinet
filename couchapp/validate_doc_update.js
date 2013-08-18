function(newDoc, oldDoc, userCtx) {
	// Helper functions
	var fail = function(message) { throw({"forbidden":message}); };
	
	// Don't validate a deletion request
	if (newDoc._deleted) { return; }
	
	var assertDate = function(fieldName, doc) {
		if ( (fieldName in newDoc) && (!Array.isArray(newDoc[fieldName]) || newDoc[fieldName].length != 3)) {
			fail(fieldName + " must be a date like [2013, 8, 22]");
		}
	};
	
	if (!("type" in newDoc)) { fail("Must have a type"); }
	switch (newDoc.type) {
		case "document":
			if (!oldDoc && !newDoc.unseen) {
				fail("All new documents must be unseen");
			}
			assertDate("uploaded", newDoc);
			assertDate("effective", newDoc);
			if ( ("tags" in newDoc) && !Array.isArray(newDoc.tags)) {
				fail("tags must be an array");
			}
			break;
		default:
			fail("Unsupported document type: "+newDoc.type);
	}
}