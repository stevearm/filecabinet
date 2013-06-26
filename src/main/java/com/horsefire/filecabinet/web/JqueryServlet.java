package com.horsefire.filecabinet.web;

@SuppressWarnings("serial")
public class JqueryServlet extends FileServlet {

	public static final String FILENAME = "jquery.js";
	public static final String PATH = "/" + FILENAME;

	public JqueryServlet() {
		super("jquery-1.9.0.min.js", "text/javascript");
	}
}
