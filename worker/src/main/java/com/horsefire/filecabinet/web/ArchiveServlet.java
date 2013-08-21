package com.horsefire.filecabinet.web;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

public class ArchiveServlet {

	private static final Logger LOG = LoggerFactory
			.getLogger(ArchiveServlet.class);

	static String createFilename(String pattern, DateTime date,
			List<String> tags) {
		pattern = pattern.replace("%y", date.toString("yyyy"))
				.replace("%m", date.toString("MM"))
				.replace("%d", date.toString("dd"));
		Collections.sort(tags);
		String tagString = Joiner.on("_").join(tags);
		return pattern.replace("%t", tagString);
	}

	/*
	 * @Override protected void doPost(HttpServletRequest req,
	 * HttpServletResponse resp) throws ServletException, IOException { String[]
	 * ids = req.getParameter("ids").split(","); String pattern =
	 * req.getParameter("pattern");
	 * 
	 * resp.setHeader("Content-Type", "application/x-zip-compressed");
	 * resp.setHeader("Content-Disposition", "attachment; filename=docs.zip");
	 * 
	 * ArchiveOutputStream out = new ArchiveOutputStream(
	 * resp.getOutputStream());
	 * 
	 * for (String id : ids) { Document document = m_cabinet.getDocument(id); if
	 * (document == null) { LOG.warn("ID {} does not exist. Skipping", id);
	 * continue; }
	 * 
	 * out.addFile(createFilename(pattern, document), document.getRawFile(),
	 * true); } out.close(); }
	 */
}
