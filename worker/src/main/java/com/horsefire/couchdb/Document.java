package com.horsefire.couchdb;

import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class Document {

	protected final JSONObject m_json;

	public Document(JSONObject json) {
		m_json = json;
	}

	public JSONObject getJsonObject() {
		return m_json;
	}

	public String getId() {
		return (String) m_json.get("_id");
	}

	public String getRev() {
		return (String) m_json.get("_rev");
	}

	public void setRev(String rev) {
		m_json.put("_rev", rev);
	}

	public boolean hasAttachment(String name) {
		return getAttachment(name) != null;
	}

	private JSONObject getAttachment(String name) {
		JSONObject attachments = (JSONObject) m_json.get("_attachments");
		if (attachments != null) {
			return (JSONObject) attachments.get(name);
		}
		return null;
	}

	public String getContentType(String name) {
		JSONObject attachment = getAttachment(name);
		if (attachment != null) {
			return attachment.get("content_type").toString();
		}
		return null;
	}

	@Override
	public String toString() {
		return m_json.toJSONString();
	}
}
