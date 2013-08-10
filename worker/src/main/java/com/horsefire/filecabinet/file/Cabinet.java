package com.horsefire.filecabinet.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.HashCodes;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class Cabinet {

	private static final Logger LOG = LoggerFactory.getLogger(Cabinet.class);

	private static String sha1(File file) throws IOException {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Somehow missing SHA-1", e);
		}
		InputStream fis = new FileInputStream(file);
		try {
			int n = 0;
			byte[] buffer = new byte[8192];
			while (n != -1) {
				n = fis.read(buffer);
				if (n > 0) {
					digest.update(buffer, 0, n);
				}
			}
			return HashCodes.fromBytes(digest.digest()).toString();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	private final File m_baseDir;
	private final Map<String, Document> m_docs = new HashMap<String, Document>();

	@Inject
	public Cabinet(@Named("cabinet") File baseDir) {
		m_baseDir = baseDir;
		if (!m_baseDir.isDirectory()) {
			m_baseDir.mkdir();
		}
	}

	public void addDocument(File file) throws IOException {
		String filename = file.getName();
		if (!filename.substring(filename.length() - 3).toLowerCase()
				.equals("pdf")) {
			throw new IOException("Unsupported file type: " + filename);
		}

		String sha1 = sha1(file);
		File dir = new File(m_baseDir, sha1.substring(0, 2));
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		dir = new File(dir, sha1.substring(2, 4));
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		File documentFile = new File(dir, sha1 + "." + Document.EXT_RAW);

		Files.copy(file, documentFile);
		if (!file.delete()) {
			LOG.error("Could not delete {}", file);
		}

		Document doc = new PdfDocument(dir, sha1);
		doc.setFilename(filename);
		if (!doc.hasThumbnail()) {
			doc.createThumbnail();
		}

		LOG.debug("Imported {} to {}", file, documentFile);
	}

	public Document getDocument(String id) throws IOException {
		try {
			File dir = new File(new File(m_baseDir, id.substring(0, 2)),
					id.substring(2, 4));
			return new PdfDocument(dir, id);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public synchronized Collection<Document> getDocuments() throws IOException {
		if (m_docs.isEmpty()) {
			findDocuments(m_baseDir, m_docs);
			LOG.info("Loaded {} documents", m_docs.size());
		}
		return Collections.unmodifiableCollection(m_docs.values());
	}

	public synchronized void refreshDocuments() throws IOException {
		LOG.info("Clearing {} docs and refreshing", m_docs.size());
		m_docs.clear();
		findDocuments(m_baseDir, m_docs);
		LOG.info("Loaded {} documents", m_docs.size());
	}

	private void findDocuments(File dir, Map<String, Document> docs)
			throws IOException {
		for (File entry : dir.listFiles()) {
			if (entry.isFile()) {
				String filename = entry.getName();
				if (filename.endsWith("." + Document.EXT_RAW)) {
					Document doc = new PdfDocument(dir, filename.substring(0,
							filename.length() - 4));
					docs.put(doc.getId(), doc);
				}
			} else if (entry.isDirectory()) {
				findDocuments(entry, docs);
			}
		}
	}
}