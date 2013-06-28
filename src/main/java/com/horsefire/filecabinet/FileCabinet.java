package com.horsefire.filecabinet;

import java.util.concurrent.atomic.AtomicReference;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.horsefire.filecabinet.web.ShutdownServlet;
import com.horsefire.filecabinet.web.WebServer;

public class FileCabinet {

	// Only used for servlets because I can't use constructor injection there
	public static final AtomicReference<Injector> INJECTOR = new AtomicReference<Injector>();

	private final WebServer m_server;

	@Inject
	public FileCabinet(WebServer server) {
		m_server = server;
	}

	public void run() throws Exception {
		m_server.start();

		while (!ShutdownServlet.GOT_REQUEST.get()) {
			Thread.sleep(1000);
		}
		m_server.shutdown();
	}

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		try {
			new JCommander(options, args);
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			return;
		}

		Injector injector = Guice.createInjector(new FcModule(options));
		INJECTOR.set(injector);
		injector.getInstance(FileCabinet.class).run();
	}
}
