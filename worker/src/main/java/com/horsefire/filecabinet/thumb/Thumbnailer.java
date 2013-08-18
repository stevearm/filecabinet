package com.horsefire.filecabinet.thumb;

import java.io.IOException;

import com.horsefire.filecabinet.MimeType;

public interface Thumbnailer {

	MimeType incomingFormat();

	MimeType outgoingFormat();

	String suggestedName();

	byte[] createThumbnail(byte[] in) throws IOException;
}
