package com.horsefire.filecabinet.file;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

class DocumentProperties {

	String filename;
	String uploaded = new DateTime().toString();
	String effective = new DateTime().toString();
	boolean unseen = true;
	Set<String> tags = new HashSet<String>();
}
