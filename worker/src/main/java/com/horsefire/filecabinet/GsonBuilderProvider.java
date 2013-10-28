package com.horsefire.filecabinet;

import com.google.gson.GsonBuilder;
import com.google.inject.Provider;

public class GsonBuilderProvider implements Provider<GsonBuilder> {

	public GsonBuilder get() {
		return new GsonBuilder();
	}
}
