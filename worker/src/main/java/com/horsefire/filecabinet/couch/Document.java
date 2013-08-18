package com.horsefire.filecabinet.couch;

import java.io.IOException;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;

@SuppressWarnings("unchecked")
public class Document extends com.horsefire.couchdb.Document {

	public Document(com.horsefire.couchdb.Document doc) {
		super(doc.getJsonObject());
	}

	public void setFilename(String filename) {
		m_json.put("filename", filename);
	}

	public boolean unseen() {
		return m_json.get("unseen") != null
				&& ((Boolean) m_json.get("unseen")).booleanValue();
	}

	public void setUnseen() throws IOException {
		m_json.put("unseen", Boolean.TRUE);
	}

	public void setUploaded(DateTime dateTime) {
		JSONArray date = new JSONArray();
		date.add(dateTime.getYear());
		date.add(dateTime.getMonthOfYear());
		date.add(dateTime.getDayOfMonth());
		m_json.put("uploaded", date);
	}

	public String getSha1() {
		return (String) m_json.get("sha1");
	}

	public void setSha1(String sha1) {
		m_json.put("sha1", sha1);
	}

	public boolean hasFailedThumb(String thumbName) {
		JSONArray failedThumbs = (JSONArray) m_json.get("failedThumbnails");
		if (failedThumbs != null) {
			for (Object failedThumb : failedThumbs) {
				if (failedThumb.toString().equals(thumbName)) {
					return true;
				}
			}
		}
		return false;
	}

	public void setFailedThumb(String thumbName) {
		JSONArray failedThumbs = (JSONArray) m_json.get("failedThumbnails");
		if (failedThumbs == null) {
			failedThumbs = new JSONArray();
			m_json.put("failedThumbnails", failedThumbs);
		}
		for (Object failedThumb : failedThumbs) {
			if (failedThumb.toString().equals(thumbName)) {
				return;
			}
		}
		failedThumbs.add(thumbName);
	}
}
