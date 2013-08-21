package com.horsefire.couchdb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.horsefire.filecabinet.MimeType;
import com.horsefire.filecabinet.couch.Attachment;

public class CouchClient {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory
			.getLogger(CouchClient.class);

	private final String m_host;
	private final String m_db;
	private final JSONParser m_parser;

	@Inject
	public CouchClient(@Named("db-host") String host,
			@Named("db-name") String db, JSONParser parser) {
		m_host = host;
		m_db = db;
		m_parser = parser;
	}

	private String baseUrl() {
		return "http://" + m_host + "/" + m_db;
	}

	private HttpResponse get(String url) throws IOException, ParseException {
		HttpGet get = new HttpGet(url);
		HttpResponse result = new DefaultHttpClient().execute(get);
		if (result.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
			return result;
		}
		result.getEntity().getContent().close();
		throw new IOException("Got " + result.getStatusLine().getStatusCode()
				+ " from GET " + get.getURI());
	}

	private JSONObject doPut(HttpPut put) throws IllegalStateException,
			IOException, ParseException {
		HttpResponse response = new DefaultHttpClient().execute(put);

		InputStream responseStream = response.getEntity().getContent();
		byte[] bytes = ByteStreams.toByteArray(responseStream);
		responseStream.close();

		String json = new String(bytes, "UTF-8");
		return (JSONObject) m_parser.parse(json);
	}

	private JSONObject getObject(String url) throws IOException, ParseException {
		HttpResponse response = get(url);
		Reader in = new InputStreamReader(response.getEntity().getContent(),
				"UTF-8");
		try {
			return (JSONObject) m_parser.parse(in);
		} finally {
			in.close();
		}
	}

	public JSONObject getView(String view) throws IOException, ParseException {
		return getObject(baseUrl() + "/_design/ui/_view/" + view);
	}

	public Document getDocument(String id) throws IOException, ParseException {
		return new Document(getObject(baseUrl() + "/" + id));
	}

	private String getUuid() throws IOException, ParseException {
		JSONObject json = getObject("http://" + m_host + "/_uuids");
		JSONArray uuids = (JSONArray) ((JSONObject) json).get("uuids");
		return uuids.get(0).toString();
	}

	public void putDocument(Document doc) throws IOException, ParseException {
		String id = doc.getId();
		if (id == null) {
			id = getUuid();
		}

		HttpPut put = new HttpPut(baseUrl() + "/" + id);
		put.setEntity(new StringEntity(doc.getJsonObject().toJSONString(),
				ContentType.APPLICATION_JSON));
		JSONObject result = doPut(put);
		if (result.get("ok") == null
				|| !((Boolean) result.get("ok")).booleanValue()) {
			throw new IOException("Failed to save doc: "
					+ result.toJSONString());
		}
		if (doc.getId() == null) {
			doc.getJsonObject().put("_id", id);
		}
		doc.setRev(result.get("rev").toString());
	}

	public Attachment getAttachment(Document doc, String attachmentName)
			throws IOException, ParseException {
		HttpResponse response = get(baseUrl() + "/" + doc.getId() + "/"
				+ attachmentName);
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

	public void putAttachment(Document doc, String name, Attachment attachment)
			throws IOException, IllegalStateException, ParseException {
		HttpPut put = new HttpPut(baseUrl() + "/" + doc.getId() + "/" + name
				+ "?rev=" + doc.getRev());
		put.setEntity(new ByteArrayEntity(attachment.content, ContentType
				.create(attachment.type.toString())));
		JSONObject result = doPut(put);
		if (result.get("ok") == null
				|| !((Boolean) result.get("ok")).booleanValue()) {
			throw new IOException("Failed to save doc: "
					+ result.toJSONString());
		}
		doc.setRev(result.get("rev").toString());
	}
}
