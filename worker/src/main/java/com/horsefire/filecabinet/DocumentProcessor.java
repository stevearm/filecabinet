package com.horsefire.filecabinet;

import java.io.IOException;
import java.io.InputStream;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.horsefire.couchdb.CouchClient;
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

	private final CouchClient m_client;
	private final ThumbnailerRegistry m_thumbnailers;
	private final FcDocument m_doc;
	private Attachment m_rawFile = null;
	private boolean m_modified = false;
	private byte[] m_brokenThumb = null;

	@Inject
	public DocumentProcessor(CouchClient client,
			ThumbnailerRegistry thumbnailers, @Assisted FcDocument doc) {
		m_client = client;
		m_thumbnailers = thumbnailers;
		m_doc = doc;
	}

	private Attachment getFile() throws IOException, ParseException {
		if (m_rawFile == null) {
			String id = m_doc.getId();
			LOG.debug("Downloading raw file for doc {}", id);
			m_rawFile = m_client.getAttachment(m_doc, "raw");
		}
		return m_rawFile;
	}

	public void process() throws IOException, ParseException {
		if (!m_doc.hasAttachment("raw")) {
			if (!m_doc.unseen()) {
				m_doc.setUnseen();
				m_client.putDocument(m_doc);
			}
			LOG.info("Doc {} has no attachment 'raw', so ending processing",
					m_doc.getId());
			return;
		}

		ensureSha1();
		ensureThumbs();

		cleanUp();
	}

	private void ensureSha1() throws IOException, ParseException {
		if (m_doc.getSha1() == null || m_doc.getSha1().isEmpty()) {
			LOG.info("Doc {} missing sha. Generating", m_doc.getId());
			String sha1 = HashTool.sha1(getFile().content);
			m_doc.setSha1(sha1);
			m_modified = true;
		}
	}

	private void ensureThumbs() throws IOException, ParseException {
		if (!m_doc.needsThumb()) {
			return;
		}

		String contentType = m_doc.getContentType("raw");
		for (Thumbnailer thumbnailer : m_thumbnailers.getThumbnailers(MimeType
				.get(contentType))) {
			final String thumbnailName = "thumb." + thumbnailer.suggestedName();
			if (m_doc.hasAttachment(thumbnailName)) {
				continue;
			}

			LOG.info("Running {} through thumbnailer {}", m_doc.getId(),
					thumbnailer.suggestedName());

			byte[] thumbnail = null;
			try {
				thumbnail = thumbnailer.createThumbnail(getFile().content);
			} catch (IOException e) {
				LOG.warn("Error creating thumbnail for {}", m_doc.getId(), e);
				thumbnail = getBrokenThumb();
			}
			LOG.debug("Uploading {} to {}", thumbnailName, m_doc.getId());
			m_modified = true;
			m_client.putAttachment(m_doc, thumbnailName, new Attachment(
					thumbnailer.outgoingFormat(), thumbnail));
		}
	}

	private byte[] getBrokenThumb() throws IOException {
		if (m_brokenThumb == null) {
			InputStream in = getClass().getClassLoader().getResourceAsStream(
					"broken.jpg");
			m_brokenThumb = ByteStreams.toByteArray(in);
			in.close();
		}
		return m_brokenThumb;
	}

	private void cleanUp() throws IOException, ParseException {
		if (m_modified) {
			m_doc.setUnseen();
			m_client.putDocument(m_doc);
		}
	}
}
