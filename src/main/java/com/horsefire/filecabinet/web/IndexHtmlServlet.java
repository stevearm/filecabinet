package com.horsefire.filecabinet.web;

@SuppressWarnings("serial")
public class IndexHtmlServlet extends FileServlet {

	public static final String FILENAME = "index.html";
	public static final String PATH = "/" + FILENAME;

	public IndexHtmlServlet() {
		super(FILENAME, "text/html");
	}
}
