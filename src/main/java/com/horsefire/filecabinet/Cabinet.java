package com.horsefire.filecabinet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.HashCodes;
import com.google.common.io.Files;
import com.horsefire.filecabinet.file.Document;
import com.horsefire.filecabinet.file.PdfDocument;

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

	public Cabinet(File baseDir) {
		m_baseDir = baseDir;
		if (!m_baseDir.isDirectory()) {
			m_baseDir.mkdir();
		}
	}

	public void addDocument(File file) throws IOException {
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

		String filename = file.getName();
		Document doc;
		if (filename.substring(filename.length() - 3).equals("pdf")) {
			doc = new PdfDocument(documentFile);
		} else {
			throw new UnsupportedOperationException("Unsupported file type: "
					+ filename);
		}

		Files.copy(file, documentFile);

		doc.setFilename(filename);
		doc.createThumbnail();

		LOG.debug("Imported {} to {}", file, documentFile);
	}
}
