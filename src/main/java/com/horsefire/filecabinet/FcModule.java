package com.horsefire.filecabinet;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import com.horsefire.filecabinet.web.CabinetServlet;
import com.horsefire.filecabinet.web.EmbeddedFileServlet;
import com.horsefire.filecabinet.web.FetchServlet;
import com.horsefire.filecabinet.web.ShutdownServlet;

public class FcModule extends ServletModule {

	private final Options m_options;

	public FcModule(Options options) {
		m_options = options;
	}

	@Override
	protected void configureServlets() {
		bind(Options.class).toInstance(m_options);
		bind(File.class).annotatedWith(Names.named("cabinet")).toInstance(
				new File("cabinet"));
		bind(File.class).annotatedWith(Names.named("desk")).toInstance(
				new File("desk"));

		bind(AtomicBoolean.class)
				.annotatedWith(Names.named("shutdown-monitor")).toInstance(
						new AtomicBoolean(false));

		serve("/shutdown").with(ShutdownServlet.class);
		serve("/cabinet").with(CabinetServlet.class);
		serve("/fetch").with(FetchServlet.class);
		serve("/").with(EmbeddedFileServlet.class);
	}
}
