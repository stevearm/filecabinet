package com.horsefire.filecabinet.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Injector;
import com.horsefire.filecabinet.FileCabinet;
import com.horsefire.filecabinet.file.Cabinet;
import com.horsefire.filecabinet.file.Document;

@SuppressWarnings("serial")
public class CabinetServlet extends HttpServlet {

	public static final String PATH = "/cabinet";

	private final Cabinet m_cabinet;

	public CabinetServlet() {
		Injector i = FileCabinet.INJECTOR.get();
		m_cabinet = i.getInstance(Cabinet.class);
	}

	private Collection<Map<String, Object>> getDocuments() throws IOException {
		Collection<Map<String, Object>> docs = new ArrayList<Map<String, Object>>();
		for (Document doc : m_cabinet.getDocuments()) {
			Map<String, Object> docInfo = new HashMap<String, Object>();
			docInfo.put("id", doc.getId());
			docInfo.put("unseen", doc.unseen());
			docInfo.put("thumb", doc.hasThumbnail());
			docs.add(docInfo);
		}
		return docs;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("docs", getDocuments());

		resp.setContentType("text/javascript");
		resp.getWriter().println(new Gson().toJson(properties));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String id = req.getParameter("id");

		String action = req.getParameter("action");
		if ("createThumbnail".equals(action)) {
			m_cabinet.getDocument(id).createThumbnail();
		} else {
			resp.sendError(HttpURLConnection.HTTP_BAD_REQUEST,
					"Unknown action parameter");
		}
	}
}
