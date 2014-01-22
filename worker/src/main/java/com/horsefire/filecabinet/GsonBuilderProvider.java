package com.horsefire.filecabinet;

import java.lang.reflect.Type;

import org.joda.time.DateTime;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.Provider;

public class GsonBuilderProvider implements Provider<GsonBuilder> {

	public GsonBuilder get() {
		return new GsonBuilder().registerTypeAdapter(DateTime.class,
				new DateTimeTypeConverter());
	}

	private static class DateTimeTypeConverter implements
			JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
		public JsonElement serialize(DateTime src, Type srcType,
				JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}

		public DateTime deserialize(JsonElement json, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			return new DateTime(json.getAsString());
		}
	}
}
