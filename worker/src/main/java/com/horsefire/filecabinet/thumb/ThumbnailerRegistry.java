package com.horsefire.filecabinet.thumb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.horsefire.filecabinet.MimeType;

@Singleton
public class ThumbnailerRegistry {

	private Map<MimeType, Collection<Thumbnailer>> m_thumbnailers = new HashMap<MimeType, Collection<Thumbnailer>>();

	@Inject
	public ThumbnailerRegistry(PdfViewThumbnailer t1, PdfBoxThumbnailer t2,
			ImageThumbnailer t3) {
		add(t1);
		add(t2);
		add(t3);
	}

	private void add(Thumbnailer thumbnailer) {
		for (MimeType mimeType : thumbnailer.incomingFormats()) {
			Collection<Thumbnailer> thumbs = m_thumbnailers.get(mimeType);
			if (thumbs == null) {
				thumbs = new ArrayList<Thumbnailer>();
				m_thumbnailers.put(mimeType, thumbs);
			}
			thumbs.add(thumbnailer);
		}
	}

	public Collection<Thumbnailer> getThumbnailers(MimeType contentType) {
		Collection<Thumbnailer> thumbs = m_thumbnailers.get(contentType);
		if (thumbs == null) {
			return Collections.emptyList();
		}
		return thumbs;
	}
}
