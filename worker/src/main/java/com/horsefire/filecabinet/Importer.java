package com.horsefire.filecabinet;

import java.io.File;
import java.io.IOException;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.inject.Inject;
import com.horsefire.couchdb.CouchClient;
import com.horsefire.couchdb.Document;
import com.horsefire.filecabinet.couch.Attachment;
import com.horsefire.filecabinet.couch.FcDocument;

public class Importer {

	private static final Logger LOG = LoggerFactory.getLogger(Importer.class);

	private final CouchClient m_client;

	@Inject
	public Importer(CouchClient client) {
		m_client = client;
	}

	private String getInboxPath() throws IOException, ParseException {
		Document document = m_client.getDocument("prefs");
		return (String) document.getJsonObject().get("inbox_path");
	}

	private String getDocId(String sha1) throws IOException, ParseException {
		JSONObject view = m_client.getView("sha1?reduce=false&key=%22" + sha1
				+ "%22");
		for (Object obj : (JSONArray) view.get("rows")) {
			JSONObject row = (JSONObject) obj;
			return (String) row.get("id");
		}
		return null;
	}

	private void importDocument(File file, String sha1) throws IOException,
			ParseException {
		FcDocument doc = new FcDocument();
		doc.setUploaded(new DateTime());
		doc.setFilename(file.getName());
		doc.setSha1(sha1);
		m_client.putDocument(doc);

		MimeType type = MimeType.guessByFilename(file.getName());
		byte[] content = Files.toByteArray(file);
		Attachment attachment = new Attachment(type, content);
		m_client.putAttachment(doc, "raw", attachment);

		file.delete();
	}

	public void run() throws IOException, ParseException {
		String inboxPath = getInboxPath();
		if (inboxPath == null) {
			LOG.info("No inbox_path defined");
			return;
		}
		File inbox = new File(inboxPath);
		if (!inbox.isDirectory()) {
			LOG.info("Inbox {} is not a directory", inbox);
			return;
		}
		for (File file : inbox.listFiles()) {
			LOG.info("Importing {}", file.getName());
			String sha1 = HashTool.sha1(file);
			String duplicateDocId = getDocId(sha1);
			if (duplicateDocId != null) {
				LOG.info("Sha1 {} found for doc id {}", sha1, duplicateDocId);
				FcDocument doc = new FcDocument(
						m_client.getDocument(duplicateDocId));
				doc.setUnseen();
				m_client.putDocument(doc);
			} else {
				LOG.info("Importing as new");
				importDocument(file, sha1);
			}
		}
	}
}
