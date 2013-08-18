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
	public ThumbnailerRegistry(PdfViewThumbnailer t1, PdfBoxThumbnailer t2) {
		add(t1);
		add(t2);
	}

	private void add(Thumbnailer thumbnailer) {
		Collection<Thumbnailer> thumbs = m_thumbnailers.get(thumbnailer
				.incomingFormat());
		if (thumbs == null) {
			thumbs = new ArrayList<Thumbnailer>();
			m_thumbnailers.put(thumbnailer.incomingFormat(), thumbs);
		}
		thumbs.add(thumbnailer);
	}

	public Collection<Thumbnailer> getThumbnailers(MimeType contentType) {
		Collection<Thumbnailer> thumbs = m_thumbnailers.get(contentType);
		if (thumbs == null) {
			return Collections.emptyList();
		}
		return thumbs;
	}
}
