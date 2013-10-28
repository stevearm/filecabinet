package com.horsefire.filecabinet;

import org.lightcouch.CouchDbClient;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class CouchDbClientProvider implements Provider<CouchDbClient> {

	private final CouchDbClientFactory m_factory;
	private final String m_db;

	@Inject
	public CouchDbClientProvider(CouchDbClientFactory factory,
			@Named("dbName") String db) {
		m_factory = factory;
		m_db = db;
	}

	public CouchDbClient get() {
		return m_factory.get(m_db);
	}
}
