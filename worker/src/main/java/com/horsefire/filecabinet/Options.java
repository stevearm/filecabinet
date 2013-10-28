package com.horsefire.filecabinet;

import com.beust.jcommander.Parameter;

public class Options {

	@Parameter(names = { "-h", "--help" }, description = "Display help", help = true)
	public boolean help = false;

	@Parameter(names = { "-v", "--version" }, description = "Display version", help = true)
	public boolean version = false;

	@Parameter(names = { "--port" }, description = "Database http port")
	public int port = 5984;

	@Parameter(names = { "--db" }, description = "Database name")
	public String dbName = "filecabinet";

	@Parameter(names = { "--username" }, description = "Database username")
	public String username;

	@Parameter(names = { "--password" }, description = "Database password")
	public String password;

	@Parameter(names = { "--vaultid" }, description = "Current vault id")
	public String vaultId;
}
