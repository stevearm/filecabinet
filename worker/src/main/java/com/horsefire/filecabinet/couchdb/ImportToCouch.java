package com.horsefire.filecabinet.couchdb;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.io.ByteStreams;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.horsefire.filecabinet.FcModule;
import com.horsefire.filecabinet.Options;
import com.horsefire.filecabinet.file.Cabinet;
import com.horsefire.filecabinet.file.Document;

public class ImportToCouch {

	private static final String HOST = "http://127.0.0.1:5984";
	private static final String DB = "filecabinet";

	private final Cabinet m_cabinet;
	private final HttpClient m_client;
	private final JSONParser m_json;

	@Inject
	public ImportToCouch(Cabinet cabinet) {
		m_cabinet = cabinet;
		m_client = new DefaultHttpClient();
		m_json = new JSONParser();
	}

	private String getUuid() throws IOException, ParseException {
		HttpGet getRequest = new HttpGet(HOST + "/_uuids");
		HttpResponse response = m_client.execute(getRequest);
		Reader in = new InputStreamReader(response.getEntity().getContent(),
				"UTF-8");
		try {
			Object parse = m_json.parse(in);
			JSONArray uuids = (JSONArray) ((JSONObject) parse).get("uuids");
			for (Object uuid : uuids) {
				return uuid.toString();
			}
		} finally {
			in.close();
		}
		throw new IOException("Failed to get a UUID");
	}

	private JSONArray convert(DateTime dateTime) {
		JSONArray date = new JSONArray();
		date.add(dateTime.getYear());
		date.add(dateTime.getMonthOfYear());
		date.add(dateTime.getDayOfMonth());
		return date;
	}

	private JSONObject doPut(HttpPut put) throws IllegalStateException,
			IOException, ParseException {
		HttpResponse response = m_client.execute(put);

		InputStream responseStream = response.getEntity().getContent();
		byte[] bytes = ByteStreams.toByteArray(responseStream);
		responseStream.close();

		String json = new String(bytes, "UTF-8");
		JSONObject responseObject = (JSONObject) m_json.parse(json);

		if (responseObject.get("ok") == null
				|| !((Boolean) responseObject.get("ok")).booleanValue()) {
			System.err.println("Got a bad reply for " + put.getURI());
			System.err.println(responseObject.toJSONString());
		}

		return responseObject;
	}

	private void importDocument(Document doc) throws IOException,
			ParseException {
		// Prepare document
		JSONObject object = new JSONObject();
		object.put("uploaded", convert(doc.getUploaded()));
		object.put("effective", convert(doc.getEffective()));
		object.put("filename", doc.getFilename());

		// Prepare tags
		JSONArray tags = new JSONArray();
		tags.addAll(doc.getTags());
		Collections.sort(tags);
		object.put("tags", tags);

		// Create document
		String url = HOST + "/" + DB + "/" + getUuid();
		HttpPut put = new HttpPut(url);
		put.setEntity(new StringEntity(object.toJSONString(),
				ContentType.APPLICATION_JSON));
		JSONObject result = doPut(put);

		// Add raw file
		put = new HttpPut(url + "/raw?rev=" + result.get("rev"));
		put.setEntity(new FileEntity(doc.getRawFile(), ContentType
				.create("application/pdf")));
		result = doPut(put);

		// Add thumbnail
		if (doc.getThumbnailFile().isFile()) {
			put = new HttpPut(url + "/thumbnail?rev=" + result.get("rev"));
			put.setEntity(new FileEntity(doc.getThumbnailFile(), ContentType
					.create("image/png")));
			result = doPut(put);
		}

	}

	public void run() throws IOException, ParseException {
		for (Document doc : m_cabinet.getDocuments()) {
			importDocument(doc);
		}
	}

	public static void main(String args[]) throws IOException, ParseException {
		Options options = new Options();
		options.debug = true;
		Injector injector = Guice.createInjector(new FcModule(options));
		injector.getInstance(ImportToCouch.class).run();
	}
}
