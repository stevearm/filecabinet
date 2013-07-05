package com.horsefire.filecabinet;

import com.beust.jcommander.Parameter;

public class Options {

	@Parameter(names = { "--debug" }, description = "Use when developing")
	public boolean debug = false;

	@Parameter(names = { "-h", "--help" }, description = "Display help", help = true)
	public boolean help = false;

	@Parameter(names = { "-v", "--version" }, description = "Display version", help = true)
	public boolean version = false;
}
