package com.horsefire.filecabinet;

import org.lightcouch.CouchDbClient;

import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.horsefire.couchdb.AttachmentManager;

public class GuiceModule extends AbstractModule {

	private final Options m_options;

	public GuiceModule(Options options) {
		m_options = options;
	}

	@Override
	protected void configure() {
		bind(Options.class).toInstance(m_options);

		bind(GsonBuilder.class).toProvider(GsonBuilderProvider.class);
		bind(CouchDbClient.class).toProvider(CouchDbClientProvider.class);
		bind(AttachmentManager.class).toProvider(
				AttachmentManagerProvider.class);

		install(new FactoryModuleBuilder()
				.build(DocumentProcessor.Factory.class));
	}
}
