package com.horsefire.filecabinet;

import com.beust.jcommander.Parameter;

public class Options {

	@Parameter(names = { "-h", "--help" }, description = "Display help", help = true)
	public boolean help = false;

	@Parameter(names = { "-v", "--version" }, description = "Display version", help = true)
	public boolean version = false;

	@Parameter(names = { "--dbHost" }, description = "Database host")
	public String dbHost = "127.0.0.1:5984";

	@Parameter(names = { "--dbName" }, description = "Database name")
	public String dbName = "filecabinet";
}
