package com.horsefire.filecabinet;

import com.beust.jcommander.Parameter;

public class Options {

	@Parameter(names = { "-h", "--help" }, description = "Display help", help = true)
	public boolean help = false;

	@Parameter(names = { "-v", "--version" }, description = "Display version", help = true)
	public boolean version = false;

	@Parameter(names = { "--port" }, description = "Database http port")
	public int port = 5984;

	@Parameter(names = { "--host" }, description = "Database host")
	public String host = "127.0.0.1";

	@Parameter(names = { "--db" }, description = "Database name")
	public String dbName = "filecabinet";

	@Parameter(names = { "--dbUsername" }, description = "Database username", required = true)
	public String username;

	@Parameter(names = { "--dbPassword" }, description = "Database password", required = true)
	public String password;

	@Parameter(names = { "--id" }, description = "Current vault id", required = true)
	public String vaultId;

	@Parameter(names = { "--maxDocs" }, description = "Max number of documents to process")
	public int maxDocs = -1;
}
