package com.horsefire.filecabinet;

import com.beust.jcommander.Parameter;

public class Options {

	@Parameter(names = { "--debug" }, description = "Use when developing")
	public boolean debug = false;
}
