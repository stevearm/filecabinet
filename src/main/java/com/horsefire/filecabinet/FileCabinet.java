package com.horsefire.filecabinet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.common.io.ByteStreams;
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

	private static void startDebug() {
		final Logger debugLogger = LoggerFactory
				.getLogger("com.horsefire.filecabinet.DebugTask");
		final Map<File, File> files = new HashMap<File, File>();
		files.put(new File("src/main/resources/web/index.html"), new File(
				"target/classes/web/index.html"));
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputStream in;
				OutputStream out;
				try {
					for (Entry<File, File> entry : files.entrySet()) {
						in = new FileInputStream(entry.getKey());
						out = new FileOutputStream(entry.getValue());
						ByteStreams.copy(in, out);
						in.close();
						out.close();
					}
				} catch (IOException e) {
					debugLogger.error("Error copying file", e);
				}
			}
		}, 5, 1000);
	}

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		try {
			new JCommander(options, args);
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			return;
		}

		if (options.debug) {
			startDebug();
		}

		Injector injector = Guice.createInjector(new FcModule(options));
		INJECTOR.set(injector);
		injector.getInstance(FileCabinet.class).run();
	}
}
