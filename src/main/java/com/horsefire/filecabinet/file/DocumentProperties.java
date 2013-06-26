package com.horsefire.filecabinet.file;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

class DocumentProperties {

	String filename;
	DateTime uploaded;
	DateTime effective;
	boolean unseen = true;
	Set<String> tags = new HashSet<String>();
}
