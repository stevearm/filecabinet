package com.horsefire.filecabinet.web;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ShutdownServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory
			.getLogger(ShutdownServlet.class);

	public static final String PATH = "/shutdown";
	public static final AtomicBoolean GOT_REQUEST = new AtomicBoolean(false);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		LOG.info("Got shutdown request");
		GOT_REQUEST.set(true);
		resp.getWriter().println("Shutting down");
	}
}
