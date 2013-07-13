package com.horsefire.filecabinet.web;

import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.servlet.GuiceFilter;

public class WebServer {

	private final Server m_server;

	@Inject
	public WebServer() {
		ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat(
				"jetty-worker-%s").build();
		ExecutorService threadPool = Executors.newFixedThreadPool(6, factory);
		m_server = new Server(new ExecutorThreadPool(threadPool));

		ServerConnector connector = new ServerConnector(m_server);
		connector.setPort(80);
		m_server.setConnectors(new Connector[] { connector });

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
