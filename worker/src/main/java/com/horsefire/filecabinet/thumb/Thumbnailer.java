package com.horsefire.filecabinet.thumb;

import java.io.IOException;
import java.util.Collection;

import com.horsefire.filecabinet.MimeType;

public interface Thumbnailer {

	Collection<MimeType> incomingFormats();

	MimeType outgoingFormat();

	String suggestedName();

	byte[] createThumbnail(byte[] in) throws IOException;
}
