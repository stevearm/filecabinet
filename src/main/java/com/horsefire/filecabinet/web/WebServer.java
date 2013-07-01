package com.horsefire.filecabinet.web;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletHandler;

import com.google.inject.Inject;

public class WebServer {

	private final Server m_server;

	@Inject
	public WebServer() {
		m_server = new Server(80);
	}

	private Handler getServletHandler() {
		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(ShutdownServlet.class,
				ShutdownServlet.PATH);
		handler.addServletWithMapping(EmbeddedFileServlet.class, "/");
		handler.addServletWithMapping(CabinetServlet.class, CabinetServlet.PATH);
		handler.addServletWithMapping(FetchServlet.class, FetchServlet.PATH);
		return handler;
	}

	public void start() throws Exception {
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { getServletHandler(),
				new DefaultHandler() });
		m_server.setHandler(handlers);
		m_server.start();
	}

	public void shutdown() throws Exception {
		m_server.stop();
	}
}
