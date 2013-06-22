package com.horsefire.filecabinet.file;

import java.util.HashSet;
import java.util.Set;

class DocumentProperties {

	String filename;
	boolean unseen = true;
	Set<String> tags = new HashSet<String>();
}
