package com.horsefire.filecabinet.couch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.google.gson.JsonObject;

public class FcDocument {

	public static final String ATTACHMENT_RAW = "raw";
	public static final String NO_THUMBNAIL = "::none";

	public String _id;
	public String _rev;
	public List<String> tags;
	public String thumbnail;
	public String filename;
	public List<Integer> uploaded;
	public String sha1;
	public String type = "document";
	public Boolean unseen = Boolean.TRUE;
	public List<Integer> effective;
	public JsonObject _attachments;

	public boolean unseen() {
		return unseen == null ? false : unseen;
	}

	public void setUnseen() throws IOException {
		unseen = Boolean.TRUE;
	}

	public void setUploaded(DateTime dateTime) {
		List<Integer> date = new ArrayList<Integer>();
		date.add(dateTime.getYear());
		date.add(dateTime.getMonthOfYear());
		date.add(dateTime.getDayOfMonth());
		uploaded = date;
	}

	public boolean hasAttachment(String attachmentName) {
		return _attachments != null && _attachments.has(attachmentName);
	}

	public String getContentType(String attachmentName) {
		return _attachments.get(attachmentName).getAsJsonObject()
				.get("content_type").getAsString();
	}
}
