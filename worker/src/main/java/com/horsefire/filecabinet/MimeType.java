package com.horsefire.filecabinet;

import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MimeType {

	private static final Map<String, MimeType> s_registry = new HashMap<String, MimeType>();

	private static MimeType get(String contentType, String[] extensions) {
		String contentTypeLower = contentType.toLowerCase();
		synchronized (s_registry) {
			MimeType mimeType = s_registry.get(contentTypeLower);
			if (mimeType == null) {
				mimeType = new MimeType(contentTypeLower, extensions);
				s_registry.put(contentTypeLower, mimeType);
			}
			return mimeType;
		}
	}

	public static final MimeType PDF = get("application/pdf",
			new String[] { "pdf" });
	public static final MimeType PNG = get("image/png", new String[] { "png" });

	public static MimeType get(String contentType) {
		return get(contentType, null);
	}

	public static MimeType guessByFilename(String filename) {
		String contentType = URLConnection.guessContentTypeFromName(filename);
		if (contentType != null) {
			return get(contentType);
		}
		return null;
	}

	private final String m_contentType;
	private final List<String> m_fileExtensions;

	private MimeType(String contentType, String[] fileExtensions) {
		m_contentType = contentType;
		if (fileExtensions == null || fileExtensions.length == 0) {
			m_fileExtensions = Collections.emptyList();
		} else {
			m_fileExtensions = Collections.unmodifiableList(Arrays
					.asList(fileExtensions));
		}
	}

	public String getFileExtension() {
		return m_fileExtensions.isEmpty() ? null : m_fileExtensions.get(0);
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	@Override
	public String toString() {
		return m_contentType;
	}
}
