package com.horsefire.filecabinet;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class CouchDbClientFactory {

	private final String m_dbHost;
	private final int m_dbPort;
	private final String m_dbUsername;
	private final String m_dbPassword;
	private final Provider<GsonBuilder> m_gsonBuilderProvider;

	@Inject
	public CouchDbClientFactory(@Named("dbHost") String dbHost,
			@Named("dbPort") Integer dbPort,
			@Named("dbUsername") String dbUsername,
			@Named("dbPassword") String dbPassword,
			Provider<GsonBuilder> gsonBuilderProvider) {
		m_dbHost = dbHost;
		m_dbPort = dbPort.intValue();
		m_dbUsername = dbUsername;
		m_dbPassword = dbPassword;
		m_gsonBuilderProvider = gsonBuilderProvider;
	}

	public CouchDbClient get(String dbName) {
		CouchDbProperties properties = new CouchDbProperties()
				.setDbName(dbName).setCreateDbIfNotExist(false)
				.setProtocol("http").setHost(m_dbHost).setPort(m_dbPort)
				.setMaxConnections(5).setConnectionTimeout(500);
		if (!m_dbUsername.isEmpty()) {
			properties.setUsername(m_dbUsername).setPassword(m_dbPassword);
		}
		CouchDbClient client = new CouchDbClient(properties);
		client.setGsonBuilder(m_gsonBuilderProvider.get());
		return client;
	}
}
