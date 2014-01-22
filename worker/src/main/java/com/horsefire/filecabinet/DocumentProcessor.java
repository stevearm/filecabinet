package com.horsefire.filecabinet;

import java.io.IOException;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.horsefire.couchdb.AttachmentManager;
import com.horsefire.filecabinet.couch.Attachment;
import com.horsefire.filecabinet.couch.FcDocument;
import com.horsefire.filecabinet.thumb.Thumbnailer;
import com.horsefire.filecabinet.thumb.ThumbnailerRegistry;

public class DocumentProcessor {

	public static interface Factory {
		DocumentProcessor create(FcDocument doc);
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(DocumentProcessor.class);

	private final CouchDbClient m_client;
	private final AttachmentManager m_attachmentManager;
	private final ThumbnailerRegistry m_thumbnailers;
	private final FcDocument m_doc;
	private Attachment m_rawFile = null;
	private boolean m_modified = false;

	@Inject
	public DocumentProcessor(CouchDbClient client,
			AttachmentManager attachmentManager,
			ThumbnailerRegistry thumbnailers, @Assisted FcDocument doc) {
		m_client = client;
		m_attachmentManager = attachmentManager;
		m_thumbnailers = thumbnailers;
		m_doc = doc;
	}

	private Attachment getFile() throws IOException {
		if (m_rawFile == null) {
			String id = m_doc._id;
			LOG.debug("Downloading raw file for doc {}", id);
			m_rawFile = m_attachmentManager.getAttachment(m_doc._id, m_doc.raw);
		}
		return m_rawFile;
	}

	public void process() throws IOException {
		if (m_doc.hasAttachment(m_doc.raw)) {
			ensureSha1();
			ensureThumbs();
		} else {
			LOG.info("Doc {} has no attachment for {}, so skipping processing",
					m_doc.raw, m_doc._id);
		}
		cleanUp();
	}

	private void ensureSha1() throws IOException {
		if (m_doc.sha1 == null || m_doc.sha1.isEmpty()) {
			LOG.info("Doc {} missing sha. Generating", m_doc._id);
			String sha1 = HashTool.sha1(getFile().content);
			m_doc.sha1 = sha1;
			m_modified = true;
		}
	}

	private void ensureThumbs() throws IOException {
		if (m_doc.thumbnail != null) {
			return;
		}

		String contentType = m_doc.getContentType(m_doc.raw);
		for (Thumbnailer thumbnailer : m_thumbnailers.getThumbnailers(MimeType
				.get(contentType))) {
			try {
				final String thumbnailName = "thumb."
						+ thumbnailer.suggestedName();
				if (m_doc.hasAttachment(thumbnailName)) {
					continue;
				}

				m_modified = true;

				LOG.info("Running {} through thumbnailer {}", m_doc._id,
						thumbnailer.suggestedName());

				byte[] thumbnail = null;
				thumbnail = thumbnailer.createThumbnail(getFile().content);
				LOG.debug("Uploading {} to {}", thumbnailName, m_doc._id);
				m_attachmentManager
						.putAttachment(m_doc._id, m_doc._rev, thumbnailName,
								new Attachment(thumbnailer.outgoingFormat(),
										thumbnail));
				FcDocument newDoc = m_client.find(FcDocument.class, m_doc._id);
				m_doc._rev = newDoc._rev;
				m_doc._attachments = newDoc._attachments;
			} catch (IOException e) {
				LOG.warn("Error creating thumbnail for {}", m_doc._id, e);
			}
		}
	}

	private void cleanUp() throws IOException {
		if (!m_doc.processed) {
			m_doc.processed = true;
			m_modified = true;
		}
		if (m_modified) {
			m_doc.seen = false;
			if (m_doc._id == null) {
				Response save = m_client.save(m_doc);
				m_doc._id = save.getId();
				m_doc._rev = save.getRev();
			} else {
				Response save = m_client.update(m_doc);
				m_doc._rev = save.getRev();
			}
		}
	}
}
