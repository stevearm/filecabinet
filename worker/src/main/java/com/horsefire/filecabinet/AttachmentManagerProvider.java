package com.horsefire.filecabinet;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.horsefire.couchdb.AttachmentManager;

public class AttachmentManagerProvider implements Provider<AttachmentManager> {

	private final String m_host;
	private final Integer m_port;
	private final String m_username;
	private final String m_password;
	private final String m_db;

	@Inject
	public AttachmentManagerProvider(Options options) {
		m_host = options.host;
		m_port = options.port;
		m_username = options.username;
		m_password = options.password;
		m_db = options.dbName;
	}

	public AttachmentManager get() {
		return new AttachmentManager(m_host, m_port, m_username, m_password,
				m_db);
	}

}
