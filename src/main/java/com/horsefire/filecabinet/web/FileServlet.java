package com.horsefire.filecabinet.web;

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

@SuppressWarnings("serial")
public abstract class FileServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory
			.getLogger(FileServlet.class);

	private final String m_resource;
	private final String m_contentType;

	public FileServlet(String resource, String contentType) {
		m_resource = resource;
		m_contentType = contentType;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		LOG.debug("Reading {}", m_resource);
		resp.setContentType(m_contentType);

		InputStream in = getClass().getClassLoader().getResourceAsStream(
				"web/" + m_resource);
		if (in == null) {
			throw new FileNotFoundException("Could not load resource: "
					+ m_resource);
		}
		OutputStream out = resp.getOutputStream();
		try {
			ByteStreams.copy(in, out);
		} finally {
			in.close();
			out.close();
		}
	}
}
