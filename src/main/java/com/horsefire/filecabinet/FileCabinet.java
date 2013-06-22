package com.horsefire.filecabinet;

import java.io.File;

public class FileCabinet {

	public static void main(String[] args) throws Exception {
		Cabinet cabinet = new Cabinet(new File("cabinet"));

		for (File file : new File("files").listFiles()) {
			cabinet.addDocument(file);
		}
	}
}
