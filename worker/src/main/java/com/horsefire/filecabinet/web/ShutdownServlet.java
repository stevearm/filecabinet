package com.horsefire.filecabinet.web;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@SuppressWarnings("serial")
@Singleton
public class ShutdownServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory
			.getLogger(ShutdownServlet.class);

	private final AtomicBoolean m_shutdown;

	@Inject
	public ShutdownServlet(@Named("shutdown-monitor") AtomicBoolean shutdown) {
		m_shutdown = shutdown;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		LOG.info("Got shutdown request");
		m_shutdown.set(true);
		resp.getWriter().println("Shutting down");
	}
}
