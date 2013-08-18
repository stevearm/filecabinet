package com.horsefire.filecabinet.couch;

import com.horsefire.filecabinet.MimeType;

public class Attachment {

	public final MimeType type;
	public final byte[] content;

	public Attachment(MimeType type, byte[] content) {
		this.type = type;
		this.content = content;
	}
}
