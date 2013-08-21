package com.horsefire.filecabinet.couch;

import java.io.IOException;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.horsefire.couchdb.Document;

@SuppressWarnings("unchecked")
public class FcDocument extends com.horsefire.couchdb.Document {

	public FcDocument(Document doc) {
		super(doc.getJsonObject());
	}

	public FcDocument() {
		super(new JSONObject());
		final JSONObject json = getJsonObject();
		json.put("type", "document");
		json.put("unseen", Boolean.TRUE);
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

	public boolean isThumbDisabled() {
		JSONObject thumb = (JSONObject) m_json.get("thumbnail");
		if (thumb != null) {
			Boolean disabled = (Boolean) thumb.get("disabled");
			return disabled != null && disabled.booleanValue();
		}
		return false;
	}

	public boolean hasFailedThumb(String thumbName) {
		JSONObject thumb = (JSONObject) m_json.get("thumbnail");
		if (thumb != null) {
			JSONArray failedGenerations = (JSONArray) thumb
					.get("failed_generations");
			if (failedGenerations != null) {
				for (Object failedName : failedGenerations) {
					if (failedName.toString().equals(thumbName)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void setFailedThumb(String thumbName) {
		JSONObject thumb = (JSONObject) m_json.get("thumbnail");
		if (thumb == null) {
			thumb = new JSONObject();
			m_json.put("thumbnail", thumb);
		}
		JSONArray failedGenerations = (JSONArray) thumb
				.get("failed_generations");
		if (failedGenerations == null) {
			failedGenerations = new JSONArray();
			thumb.put("failed_generations", failedGenerations);
		}
		for (Object failedThumb : failedGenerations) {
			if (failedThumb.toString().equals(thumbName)) {
				return;
			}
		}
		failedGenerations.add(thumbName);
	}
}