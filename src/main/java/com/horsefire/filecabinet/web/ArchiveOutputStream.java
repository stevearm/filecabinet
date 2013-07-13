package com.horsefire.filecabinet.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

public class ArchiveOutputStream {

	private static final Logger LOG = LoggerFactory
			.getLogger(ArchiveOutputStream.class);

	private final ZipOutputStream m_out;
	private final Set<String> m_usedFilenames = new HashSet<String>();

	public ArchiveOutputStream(OutputStream out) {
		m_out = new ZipOutputStream(out);
	}

	public void addFile(String filename, File file) throws IOException {
		addFile(filename, file, false);
	}

	public void addFile(String filename, File file, boolean renameDuplicates)
			throws IOException {
		LOG.debug("Adding {} to archive as {}", file, filename);
		if (renameDuplicates) {
			int index = filename.lastIndexOf('.');
			String pre = filename;
			String post = "";
			if (index != -1) {
				pre = filename.substring(0, index);
				post = filename.substring(index);
			}
			int i = 1;
			while (m_usedFilenames.contains(filename)) {
				filename = pre + '_' + i++ + post;
			}
			if (i > 1) {
				LOG.debug("Renaming file to {}", filename);
			}
			m_usedFilenames.add(filename);
		}
		m_out.putNextEntry(new ZipEntry(filename));
		Files.copy(file, m_out);
		m_out.closeEntry();
	}

	public void close() throws IOException {
		m_out.close();
	}
}
