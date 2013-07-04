package com.horsefire.filecabinet.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.horsefire.filecabinet.file.Cabinet;
import com.horsefire.filecabinet.file.Document;

@SuppressWarnings("serial")
@Singleton
public class FetchServlet extends HttpServlet {

	private final Cabinet m_cabinet;

	@Inject
	public FetchServlet(Cabinet cabinet) {
		m_cabinet = cabinet;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String id = req.getParameter("id");
		String type = req.getParameter("type");
		if (id == null || id.isEmpty() || type == null || type.isEmpty()) {
			throw new InvalidParameterException("Need id and type");
		}

		Document doc = m_cabinet.getDocument(id);
		if (doc == null) {
			throw new FileNotFoundException(id + " does not exist");
		}

		if ("thumb".equals(type)) {
			resp.setContentType("image/png");
			InputStream in = new FileInputStream(doc.getThumbnailFile());
			OutputStream out = resp.getOutputStream();
			ByteStreams.copy(in, out);
			in.close();
			out.close();
		} else if ("raw".equals(type)) {
			resp.setContentType("application/pdf");
			InputStream in = new FileInputStream(doc.getRawFile());
			OutputStream out = resp.getOutputStream();
			ByteStreams.copy(in, out);
			in.close();
			out.close();
		} else {
			throw new InvalidParameterException("Invalid type: " + type);
		}
	}
}
