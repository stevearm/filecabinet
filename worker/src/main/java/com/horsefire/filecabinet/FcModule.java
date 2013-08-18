package com.horsefire.filecabinet;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class FcModule extends AbstractModule {

	private final Options m_options;

	public FcModule(Options options) {
		m_options = options;
	}

	@Override
	protected void configure() {
		bind(Options.class).toInstance(m_options);

		File cabinet = new File("files");
		File desk = new File("incoming");

		if (m_options.debug) {
			File target = new File("target");
			cabinet = new File(target, "files");
			desk = new File(target, "incoming");
		}

		bind(File.class).annotatedWith(Names.named("cabinet")).toInstance(
				cabinet);
		bind(File.class).annotatedWith(Names.named("desk")).toInstance(desk);

		bind(AtomicBoolean.class)
				.annotatedWith(Names.named("shutdown-monitor")).toInstance(
						new AtomicBoolean(false));
	}
}
