package com.horsefire.filecabinet.web;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.horsefire.filecabinet.file.Cabinet;
import com.horsefire.filecabinet.file.Document;

@SuppressWarnings("serial")
@Singleton
public class CabinetServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory
			.getLogger(CabinetServlet.class);

	private final Cabinet m_cabinet;
	private final File m_cabinetPath;
	private final File m_deskPath;

	@Inject
	public CabinetServlet(Cabinet cabinet, @Named("cabinet") File cabinetPath,
			@Named("desk") File deskPath) {
		m_cabinet = cabinet;
		m_cabinetPath = cabinetPath;
		m_deskPath = deskPath;
	}

	private Map<String, Map<String, Object>> getDocuments() throws IOException {
		Map<String, Map<String, Object>> docs = new HashMap<String, Map<String, Object>>();
		for (Document doc : m_cabinet.getDocuments()) {
			Map<String, Object> docInfo = new HashMap<String, Object>();

			docInfo.put("id", doc.getId());
			docInfo.put("unseen", doc.unseen());
			docInfo.put("thumb", doc.hasThumbnail());
			docInfo.put("filename", doc.getFilename());
			docInfo.put("tags", doc.getTags());
			docInfo.put("uploaded", doc.getUploaded().toString("yyyy-MM-dd"));
			docInfo.put("effective", doc.getEffective().toString("yyyy-MM-dd"));

			docs.put(doc.getId(), docInfo);
		}
		return docs;
	}

	private Set<String> getTags() throws IOException {
		Set<String> tags = new HashSet<String>();
		for (Document doc : m_cabinet.getDocuments()) {
			tags.addAll(doc.getTags());
		}
		return tags;
	}

	private Map<String, String> getPaths() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("cabinet", m_cabinetPath.getAbsolutePath());
		result.put("desk", m_deskPath.getAbsolutePath());
		return result;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("docs", getDocuments());
		properties.put("tags", getTags());
		properties.put("paths", getPaths());

		resp.setContentType("text/javascript");
		resp.getWriter().println(new Gson().toJson(properties));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String id = req.getParameter("id");

		Document doc = m_cabinet.getDocument(id);
		if (doc == null) {
			resp.sendError(HttpURLConnection.HTTP_NOT_FOUND,
					"That file doesn't exist");
			return;
		}

		String action = req.getParameter("action");
		if ("createThumbnail".equals(action)) {
			LOG.debug("Generating thumbnail for {}", doc.getId());
			doc.createThumbnail();
			resp.setContentType("text/javascript");
			resp.getWriter().print("{}");
		} else if ("saveDoc".equals(action)) {
			Set<String> tagSet = new HashSet<String>();
			String[] parameterValues = req.getParameterValues("tags");
			if (parameterValues != null) {
				for (String tag : parameterValues) {
					tagSet.add(tag.toLowerCase().trim());
				}
			}
			if (!doc.getTags().equals(tagSet)) {
				doc.setTags(tagSet);
				LOG.debug("Saving doc {} tags {}", doc.getId(), tagSet);
			}

			if (doc.unseen()) {
				if (!Boolean.parseBoolean(req.getParameter("unseen"))) {
					doc.setSeen();
					LOG.debug("Setting doc {} as seen", doc.getId());
				}
			}

			DateTime effectiveDate = new DateTime(req.getParameter("effective"));
			if (!doc.getEffective().equals(effectiveDate)) {
				LOG.debug("Setting {} to effective date {}", doc.getId(),
						effectiveDate);
				doc.setEffective(effectiveDate);
			}

			resp.setContentType("text/javascript");
			resp.getWriter().print("{}");
		} else {
			resp.sendError(HttpURLConnection.HTTP_BAD_REQUEST,
					"Unknown action parameter");
		}
	}
}
