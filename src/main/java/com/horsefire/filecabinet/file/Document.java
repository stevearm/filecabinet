package com.horsefire.filecabinet.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;

public abstract class Document {

	public static final String EXT_RAW = "raw";
	public static final String EXT_THUMBNAIL = "png";

	private final File m_rawFile;
	private final File m_metaFile;
	private final File m_thumbnailFile;
	private final DocumentProperties m_props;

	public Document(File rawFile) throws IOException {
		m_rawFile = rawFile;

		if (m_rawFile.getName().length() != 44
				|| !m_rawFile.getName().substring(40).equals("." + EXT_RAW)) {
			throw new FileNotFoundException(
					"Expected raw file to be named [sha1].raw: "
							+ m_rawFile.getName());
		}

		String sha1 = m_rawFile.getName().substring(0, 40);
		m_metaFile = new File(m_rawFile.getParentFile(), sha1 + ".json");
		if (m_metaFile.isFile()) {
			Reader r = new FileReader(m_metaFile);
			try {
				m_props = new Gson().fromJson(r, DocumentProperties.class);
			} finally {
				r.close();
			}
		} else {
			m_props = new DocumentProperties();
		}

		m_thumbnailFile = new File(m_rawFile.getParentFile(), sha1 + "."
				+ EXT_THUMBNAIL);
	}

	private void saveProps() throws IOException {
		Writer w = new FileWriter(m_metaFile);
		try {
			w.write(new Gson().toJson(m_props));
		} finally {
			w.close();
		}
	}

	public File getRawFile() {
		return m_rawFile;
	}

	public File getMetaFile() {
		return m_metaFile;
	}

	public File getThumbnailFile() {
		return m_thumbnailFile;
	}

	public abstract void createThumbnail() throws IOException;

	public String getFilename() {
		return m_props.filename;
	}

	public void setFilename(String filename) throws IOException {
		m_props.filename = filename;
		saveProps();
	}

	public boolean unseen() {
		return m_props.unseen;
	}

	public void setSeen() throws IOException {
		m_props.unseen = false;
		saveProps();
	}

	public Set<String> getTags() {
		return new HashSet<String>(m_props.tags);
	}

	public void setTags(Set<String> tags) throws IOException {
		m_props.tags = tags;
		saveProps();
	}
}
