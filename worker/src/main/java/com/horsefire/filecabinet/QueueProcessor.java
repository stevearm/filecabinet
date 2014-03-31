package com.horsefire.filecabinet;

import java.io.IOException;
import java.util.List;

import org.lightcouch.NoDocumentException;
import org.lightcouch.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.horsefire.filecabinet.DocumentProcessor.Factory;
import com.horsefire.filecabinet.couch.FcDocument;

public class QueueProcessor {

	private static final Logger LOG = LoggerFactory
			.getLogger(QueueProcessor.class);

	private final CouchDbClientFactory m_clientFactory;
	private final Factory m_docProcessorFactory;
	private final String m_dbName;
	private final int m_maxDocs;

	@Inject
	public QueueProcessor(CouchDbClientFactory clientFactory,
			DocumentProcessor.Factory docProcessorFactory, Options options) {
		m_clientFactory = clientFactory;
		m_docProcessorFactory = docProcessorFactory;
		m_dbName = options.dbName;
		m_maxDocs = options.maxDocs;
	}

	public void run() throws IOException {
		View view = m_clientFactory.get(m_dbName).view("ui/worker_queue")
				.includeDocs(true).reduce(false);
		if (m_maxDocs != -1) {
			view.limit(m_maxDocs);
		}
		try {
			List<FcDocument> docs = view.query(FcDocument.class);
			LOG.info("{} documents to process", docs.size());
			for (FcDocument doc : docs) {
				LOG.info("Processing id {}", doc._id);
				m_docProcessorFactory.create(doc).process();
			}
		} catch (NoDocumentException e) {
			LOG.error("{} db doesn't have a view called ui/worker_queue",
					m_dbName);
		}
	}
}
