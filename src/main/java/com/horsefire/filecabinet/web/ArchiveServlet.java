package com.horsefire.filecabinet.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
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

	static String createFilename(String pattern, Document doc) {
		DateTime date = doc.getEffective();
		pattern = pattern.replace("%y", date.toString("yyyy"))
				.replace("%m", date.toString("MM"))
				.replace("%d", date.toString("dd"));
		List<String> tags = new ArrayList<String>(doc.getTags());
		Collections.sort(tags);
		String tagString = Joiner.on("_").join(tags);
		return pattern.replace("%t", tagString);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String[] ids = req.getParameter("ids").split(",");
		String pattern = req.getParameter("pattern");

		resp.setHeader("Content-Type", "application/x-zip-compressed");
		resp.setHeader("Content-Disposition", "attachment; filename=docs.zip");

		ArchiveOutputStream out = new ArchiveOutputStream(
				resp.getOutputStream());

		for (String id : ids) {
			Document document = m_cabinet.getDocument(id);
			if (document == null) {
				LOG.warn("ID {} does not exist. Skipping", id);
				continue;
			}

			out.addFile(createFilename(pattern, document),
					document.getRawFile(), true);
		}
		out.close();
	}
}
