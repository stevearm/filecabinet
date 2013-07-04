package com.horsefire.filecabinet;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.horsefire.filecabinet.file.Cabinet;

public class Importer {

	private static final Logger LOG = LoggerFactory.getLogger(Importer.class);

	private final Cabinet m_cabinet;
	private final File m_desk;
	private final Timer m_timer = new Timer();

	@Inject
	public Importer(Cabinet cabinet, @Named("desk") File desk) {
		m_cabinet = cabinet;
		m_desk = desk;
	}

	public void start() {
		m_timer.schedule(new TimerTask() {
			@Override
			public void run() {
				for (File file : m_desk.listFiles()) {
					try {
						m_cabinet.addDocument(file);
					} catch (IOException e) {
						LOG.error("Error importing {}", file, e);
					}
				}
			}
		}, 5, 1000);
	}

	public void shutdown() {
		m_timer.cancel();
	}
}
