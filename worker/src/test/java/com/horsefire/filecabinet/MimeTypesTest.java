package com.horsefire.filecabinet;

import junit.framework.TestCase;

public class MimeTypesTest extends TestCase {

	public void testPng() {
		assertEquals(MimeType.PNG, MimeType.guessByFilename("test.png"));
		assertEquals(MimeType.PNG, MimeType.guessByFilename("test.PNG"));
	}

	public void testPdf() {
		assertEquals(MimeType.PDF, MimeType.guessByFilename("test.pdf"));
		assertEquals(MimeType.PDF, MimeType.guessByFilename("test.PDF"));
	}
}
