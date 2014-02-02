package com.horsefire.filecabinet;

import org.lightcouch.CouchDbClient;

import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

public class GuiceModule extends AbstractModule {

	private final Integer m_dbPort;
	private final String m_dbName;
	private final String m_dbUsername;
	private final String m_dbPassword;
	private final String m_vaultId;
	private final int m_maxDocs;

	public GuiceModule(int port, String dbName, String username,
			String password, String vaultId, int maxDocs) {
		m_dbPort = Integer.valueOf(port);
		m_dbName = dbName;
		m_dbUsername = username;
		m_dbPassword = password;
		m_vaultId = vaultId;
		m_maxDocs = maxDocs;
	}

	private void bindMemberConstants() {
		bind(Integer.class).annotatedWith(Names.named("dbPort")).toInstance(
				m_dbPort);
		bind(String.class).annotatedWith(Names.named("dbName")).toInstance(
				m_dbName);
		bind(String.class).annotatedWith(Names.named("dbUsername")).toInstance(
				(m_dbUsername == null) ? "" : m_dbUsername);
		bind(String.class).annotatedWith(Names.named("dbPassword")).toInstance(
				(m_dbPassword == null) ? "" : m_dbPassword);
		bind(String.class).annotatedWith(Names.named("vaultId")).toInstance(
				(m_vaultId == null) ? "" : m_vaultId);
		bind(Integer.class).annotatedWith(Names.named("maxDocs")).toInstance(
				m_maxDocs);
	}

	@Override
	protected void configure() {
		bindMemberConstants();

		bind(String.class).annotatedWith(Names.named("dbHost")).toInstance(
				"127.0.0.1");

		bind(GsonBuilder.class).toProvider(GsonBuilderProvider.class);
		bind(CouchDbClient.class).toProvider(CouchDbClientProvider.class);

		install(new FactoryModuleBuilder()
				.build(DocumentProcessor.Factory.class));
	}
}
