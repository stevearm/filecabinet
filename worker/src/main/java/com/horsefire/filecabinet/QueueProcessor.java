package com.horsefire.filecabinet;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.horsefire.couchdb.CouchClient;
import com.horsefire.filecabinet.DocumentProcessor.Factory;
import com.horsefire.filecabinet.couch.Document;

public class QueueProcessor {

	private static final Logger LOG = LoggerFactory
			.getLogger(QueueProcessor.class);

	private final CouchClient m_client;
	private final Factory m_docProcessorFactory;

	@Inject
	public QueueProcessor(CouchClient client,
			DocumentProcessor.Factory docProcessorFactory) {
		m_client = client;
		m_docProcessorFactory = docProcessorFactory;
	}

	public void run() throws IOException, ParseException {
		JSONObject queue = m_client.getView("worker_queue?limit=5");
		JSONArray rows = (JSONArray) queue.get("rows");
		LOG.info("{} documents to process", rows.size());
		for (Object row : rows) {
			String id = ((JSONObject) row).get("id").toString();
			LOG.info("Processing id {}", id);
			Document doc = new Document(m_client.getDocument(id));
			m_docProcessorFactory.create(doc).process();
		}
	}
}
