package com.horsefire.filecabinet;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

public class FcModule extends AbstractModule {

	private final Options m_options;

	public FcModule(Options options) {
		m_options = options;
	}

	@Override
	protected void configure() {
		bind(Options.class).toInstance(m_options);

		bind(String.class).annotatedWith(Names.named("db-host")).toInstance(
				m_options.dbHost);
		bind(String.class).annotatedWith(Names.named("db-name")).toInstance(
				m_options.dbName);

		install(new FactoryModuleBuilder()
				.build(DocumentProcessor.Factory.class));
	}
}
