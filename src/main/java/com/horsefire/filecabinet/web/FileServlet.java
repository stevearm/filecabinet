package com.horsefire.filecabinet.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.inject.Injector;
import com.horsefire.filecabinet.FileCabinet;
import com.horsefire.filecabinet.Options;

@SuppressWarnings("serial")
public abstract class FileServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory
			.getLogger(FileServlet.class);

	private final String m_resource;
	private final String m_contentType;
	private final boolean m_debug;

	public FileServlet(String resource, String contentType) {
		m_resource = resource;
		m_contentType = contentType;

		Injector i = FileCabinet.INJECTOR.get();
		m_debug = i.getInstance(Options.class).debug;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		LOG.debug("Reading {}", m_resource);
		resp.setContentType(m_contentType);

		InputStream in = null;
		OutputStream out = null;
		try {
			if (m_debug) {
				in = new FileInputStream(new File("src/main/resources/web/"
						+ m_resource));
			} else {
				in = getClass().getClassLoader().getResourceAsStream(
						"web/" + m_resource);
			}
			if (in == null) {
				throw new FileNotFoundException("Could not load resource: "
						+ m_resource);
			}
			out = resp.getOutputStream();
			ByteStreams.copy(in, out);
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}
}
