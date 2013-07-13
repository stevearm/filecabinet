package com.horsefire.filecabinet.web;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.horsefire.filecabinet.file.Cabinet;
import com.horsefire.filecabinet.file.Document;

@SuppressWarnings("serial")
@Singleton
public class ArchiveServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory
			.getLogger(ArchiveServlet.class);

	private final Cabinet m_cabinet;

	@Inject
	public ArchiveServlet(Cabinet cabinet) {
		m_cabinet = cabinet;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String[] ids = req.getParameter("ids").split(",");

		resp.setHeader("Content-Type", "application/x-zip-compressed");
		resp.setHeader("Content-Disposition", "attachment; filename=docs.zip");

		ZipOutputStream out = new ZipOutputStream(resp.getOutputStream());

		for (String id : ids) {
			Document document = m_cabinet.getDocument(id);
			if (document == null) {
				LOG.warn("ID {} does not exist. Skipping", id);
				continue;
			}

			out.putNextEntry(new ZipEntry(document.getFilename()));
			Files.copy(document.getRawFile(), out);
			out.closeEntry();
		}
		out.close();
	}
}
