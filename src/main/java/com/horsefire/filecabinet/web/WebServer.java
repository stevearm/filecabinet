package com.horsefire.filecabinet.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.Inject;
import com.google.inject.servlet.GuiceFilter;

public class WebServer {

	private final Server m_server;

	@Inject
	public WebServer() {
		m_server = new Server(80);

		ServletContextHandler handler = new ServletContextHandler(m_server,
				"/", ServletContextHandler.NO_SECURITY
						| ServletContextHandler.NO_SESSIONS);
		handler.addFilter(GuiceFilter.class, "/*",
				EnumSet.allOf(DispatcherType.class));
		handler.addServlet(DefaultServlet.class, "/");
	}

	public void start() throws Exception {
		m_server.start();
	}

	public void shutdown() throws Exception {
		m_server.stop();
	}
}
