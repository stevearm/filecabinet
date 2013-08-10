package com.horsefire.filecabinet.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.horsefire.filecabinet.Options;

@SuppressWarnings("serial")
@Singleton
public class EmbeddedFileServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory
			.getLogger(EmbeddedFileServlet.class);

	private final boolean m_debug;

	@Inject
	public EmbeddedFileServlet(Options options) {
		m_debug = options.debug;
	}

	private void readFile(String filename, String contentType,
			HttpServletResponse resp) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			if (m_debug) {
				in = new FileInputStream(new File("src/main/resources/web/"
						+ filename));
			} else {
				in = getClass().getClassLoader().getResourceAsStream(
						"web/" + filename);
			}

			if (in == null) {
				resp.sendError(HttpURLConnection.HTTP_NOT_FOUND,
						"Could not load resource: " + filename);
				return;
			}

			resp.setContentType(contentType);
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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String file = req.getParameter("file");
		if (file == null || file.isEmpty()) {
			file = "index.html";
			LOG.debug("Defaulting blank request to {}", file);
		}
		LOG.debug("Reading embedded {}", file);

		String extension = file.substring(file.lastIndexOf('.') + 1);
		if ("html".equals(extension)) {
			readFile(file, "text/html", resp);
		} else if ("js".equals(extension)) {
			readFile(file, "text/javascript", resp);
		} else if ("css".equals(extension)) {
			readFile(file, "text/css", resp);
		} else {
			LOG.error("Unknown file extension {}", file);
			resp.sendError(HttpURLConnection.HTTP_BAD_REQUEST,
					"Unknown file type: " + extension);
		}
	}
}
