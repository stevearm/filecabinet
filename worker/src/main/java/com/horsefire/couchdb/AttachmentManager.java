package com.horsefire.couchdb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.horsefire.filecabinet.MimeType;
import com.horsefire.filecabinet.couch.Attachment;

public class AttachmentManager {

	private static final Logger LOG = LoggerFactory
			.getLogger(AttachmentManager.class);

	private final String m_url;

	public AttachmentManager(String host, Integer port, String username,
			String password, String db) {
		StringBuilder b = new StringBuilder("http://");
		if (username != null && password != null) {
			b.append(username).append(":").append(password).append("@");
		}
		b.append(host);
		if (port != null) {
			b.append(":").append(port);
		}
		b.append("/").append(db);
		m_url = b.toString();
	}

	private HttpResponse get(String url) throws IOException {
		HttpGet get = new HttpGet(url);
		HttpResponse result = new DefaultHttpClient().execute(get);
		if (result.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
			return result;
		}
		result.getEntity().getContent().close();
		throw new IOException("Got " + result.getStatusLine().getStatusCode()
				+ " from GET " + get.getURI());
	}

	private JsonObject doPut(HttpPut put) throws IllegalStateException,
			IOException {
		HttpResponse response = new DefaultHttpClient().execute(put);

		InputStream responseStream = response.getEntity().getContent();
		byte[] bytes = ByteStreams.toByteArray(responseStream);
		responseStream.close();

		String json = new String(bytes, "UTF-8");
		return new JsonParser().parse(json).getAsJsonObject();
	}

	public Attachment getAttachment(String id, String attachmentName)
			throws IOException {
		HttpResponse response = get(m_url + "/" + id + "/" + attachmentName);
		String contentType = response.getHeaders("Content-Type")[0].getValue();
		MimeType mimeType = MimeType.get(contentType);
		InputStream in = response.getEntity().getContent();
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteStreams.copy(in, out);

			return new Attachment(mimeType, out.toByteArray());
		} finally {
			in.close();
		}
	}

	public void putAttachment(String id, String rev, String name,
			Attachment attachment) throws IOException, IllegalStateException {
		if (attachment.content == null || attachment.content.length == 0) {
			throw new NullPointerException("Cannot upload a null payload");
		}
		HttpPut put = new HttpPut(m_url + "/" + id + "/" + name + "?rev=" + rev);
		put.setEntity(new ByteArrayEntity(attachment.content, ContentType
				.create(attachment.type.toString())));
		JsonObject result = doPut(put);
		LOG.debug("Uploaded {} to {} and got {}", new Object[] { name, id,
				result });
		if (!result.has("ok") || !result.get("ok").getAsBoolean()) {
			throw new IOException("Failed to save doc: " + result.toString());
		}
	}
}
