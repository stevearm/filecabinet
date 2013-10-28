package com.horsefire.filecabinet;

import java.io.File;
import java.io.IOException;

import org.joda.time.DateTime;
import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;
import org.lightcouch.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.horsefire.couchdb.AttachmentManager;
import com.horsefire.filecabinet.couch.Attachment;
import com.horsefire.filecabinet.couch.FcDocument;

public class Importer {

	private static final Logger LOG = LoggerFactory.getLogger(Importer.class);

	private final CouchDbClient m_client;
	private final AttachmentManager m_attachmentManager;
	private final String m_vaultId;

	@Inject
	public Importer(CouchDbClient client, AttachmentManager attachmentManager,
			@Named("vaultId") String vaultId) {
		m_client = client;
		m_attachmentManager = attachmentManager;
		m_vaultId = vaultId;
	}

	private String getInboxPath() throws IOException {
		try {
			return m_client.find(PrefsDocument.class, "prefs").inbox_path
					.get(m_vaultId);
		} catch (NoDocumentException e) {
			return null;
		}
	}

	private String getDocId(String sha1) throws IOException {
		View view = m_client.view("ui/sha1");
		view.reduce(false);
		view.key(sha1);
		view.includeDocs(true);
		for (FcDocument doc : view.query(FcDocument.class)) {
			return doc._id;
		}
		return null;
	}

	private void importDocument(File file, String sha1) throws IOException {
		FcDocument doc = new FcDocument();
		doc.setUploaded(new DateTime());
		doc.filename = file.getName();
		doc.sha1 = sha1;
		Response save = m_client.save(doc);
		if (save.getError() != null && !save.getError().isEmpty()) {
			throw new IOException(save.getError());
		}
		doc._id = save.getId();
		doc._rev = save.getRev();

		MimeType type = MimeType.guessByFilename(file.getName());
		if (type == null) {
			throw new IOException("Unsupported file type: " + file.getName());
		}
		byte[] content = Files.toByteArray(file);
		Attachment attachment = new Attachment(type, content);
		m_attachmentManager.putAttachment(doc._id, doc._rev, "raw", attachment);

		file.delete();
	}

	public void run() throws IOException {
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
				FcDocument doc = m_client
						.find(FcDocument.class, duplicateDocId);
				doc.setUnseen();
				m_client.save(doc);
				file.delete();
			} else {
				LOG.info("Importing as new");
				importDocument(file, sha1);
			}
		}
	}
}
