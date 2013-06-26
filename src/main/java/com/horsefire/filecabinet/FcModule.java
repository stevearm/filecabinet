package com.horsefire.filecabinet;

import java.io.File;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class FcModule extends AbstractModule {

	private final Options m_options;

	public FcModule(Options options) {
		m_options = options;
	}

	@Override
	protected void configure() {
		bind(File.class).annotatedWith(Names.named("cabinet")).toInstance(
				new File("cabinet"));
		bind(File.class).annotatedWith(Names.named("desk")).toInstance(
				new File("desk"));
	}

}
