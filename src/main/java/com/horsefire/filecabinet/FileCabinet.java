package com.horsefire.filecabinet;

import java.util.concurrent.atomic.AtomicBoolean;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.horsefire.filecabinet.web.WebServer;

public class FileCabinet {

	private final WebServer m_server;
	private final AtomicBoolean m_shutdown;

	@Inject
	public FileCabinet(WebServer server,
			@Named("shutdown-monitor") AtomicBoolean shutdown) {
		m_server = server;
		m_shutdown = shutdown;
	}

	public void run() throws Exception {
		m_server.start();

		while (!m_shutdown.get()) {
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

		Guice.createInjector(new FcModule(options))
				.getInstance(FileCabinet.class).run();
	}
}
