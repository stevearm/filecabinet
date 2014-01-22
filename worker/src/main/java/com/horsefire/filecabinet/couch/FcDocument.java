package com.horsefire.filecabinet.couch;

import java.util.List;

import org.joda.time.DateTime;

import com.google.gson.JsonObject;

public class FcDocument {

	public String _id;
	public String _rev;
	public List<String> tags;
	public String raw;
	public String thumbnail;
	public DateTime uploaded;
	public String sha1;
	public String type = "document";
	public DateTime effective;
	public boolean seen;
	public boolean processed;
	public JsonObject _attachments;

	public boolean hasAttachment(String attachmentName) {
		return _attachments != null && _attachments.has(attachmentName);
	}

	public String getContentType(String attachmentName) {
		return _attachments.get(attachmentName).getAsJsonObject()
				.get("content_type").getAsString();
	}
}
