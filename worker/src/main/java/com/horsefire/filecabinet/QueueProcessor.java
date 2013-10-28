package com.horsefire.filecabinet;

import java.io.IOException;
import java.util.List;

import org.lightcouch.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.horsefire.filecabinet.DocumentProcessor.Factory;
import com.horsefire.filecabinet.couch.FcDocument;

public class QueueProcessor {

	private static final Logger LOG = LoggerFactory
			.getLogger(QueueProcessor.class);

	private final CouchDbClientFactory m_clientFactory;
	private final Factory m_docProcessorFactory;
	private final String m_dbName;

	@Inject
	public QueueProcessor(CouchDbClientFactory clientFactory,
			DocumentProcessor.Factory docProcessorFactory,
			@Named("dbName") String dbName) {
		m_clientFactory = clientFactory;
		m_docProcessorFactory = docProcessorFactory;
		m_dbName = dbName;
	}

	public void run() throws IOException {
		View view = m_clientFactory.get(m_dbName).view("ui/worker_queue");
		view.limit(5);
		view.includeDocs(true);
		List<FcDocument> docs = view.query(FcDocument.class);
		LOG.info("{} documents to process", docs.size());
		for (FcDocument doc : docs) {
			LOG.info("Processing id {}", doc._id);
			m_docProcessorFactory.create(doc).process();
		}
	}
}
