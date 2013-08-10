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

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public abstract class Document {

	private static final Logger LOG = LoggerFactory.getLogger(Document.class);

	public static final String EXT_RAW = "raw";
	public static final String EXT_THUMBNAIL = "png";

	private final String m_id;
	private final File m_rawFile;
	private final File m_metaFile;
	private final File m_thumbnailFile;
	private final DocumentProperties m_props;

	public Document(File dir, String id) throws IOException {
		m_id = id;

		m_rawFile = new File(dir, id + "." + EXT_RAW);
		if (!m_rawFile.isFile()) {
			throw new FileNotFoundException("Raw file must exist: " + m_rawFile);
		}

		m_metaFile = new File(dir, m_id + ".json");
		if (m_metaFile.isFile()) {
			Reader r = new FileReader(m_metaFile);
			try {
				m_props = new Gson().fromJson(r, DocumentProperties.class);
			} catch (RuntimeException e) {
				LOG.error("Could not deserialize {}", m_metaFile, e);
				throw e;
			} finally {
				r.close();
			}
		} else {
			m_props = new DocumentProperties();
		}

		m_thumbnailFile = new File(dir, m_id + "." + EXT_THUMBNAIL);
	}

	public String getId() {
		return m_id;
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

	public boolean hasThumbnail() {
		return getThumbnailFile().isFile();
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

	public DateTime getUploaded() {
		return new DateTime(m_props.uploaded);
	}

	public DateTime getEffective() {
		return new DateTime(m_props.effective);
	}

	public void setEffective(DateTime effective) throws IOException {
		m_props.effective = effective.toString();
		saveProps();
	}
}
