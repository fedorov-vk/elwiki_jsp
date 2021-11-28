package org.elwiki.data.persist.json.deserialize;

import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DeserialiseStuff {

	public DeserialiseStuff() {
		super();
	}

	protected String getString(JsonObject jsonObject, String optionName) {
		Optional<JsonElement> opt = Optional.ofNullable(jsonObject.get(optionName));
		return opt.map(j -> j.getAsString()).orElse("");
	}

	protected int getInt(JsonObject jsonObject, String optionName) {
		Optional<JsonElement> opt = Optional.ofNullable(jsonObject.get(optionName));
		return opt.map(j -> j.getAsInt()).orElse(0);
	}

	protected long getLong(JsonObject jsonObject, String optionName) {
		Optional<JsonElement> opt = Optional.ofNullable(jsonObject.get(optionName));
		return opt.map(j -> j.getAsLong()).orElse(0L);
	}

	protected boolean getBoolean(JsonObject jsonObject, String optionName) {
		Optional<JsonElement> opt = Optional.ofNullable(jsonObject.get(optionName));
		return opt.map(j -> j.getAsBoolean()).orElse(false);
	}

	protected JsonArray getArray(JsonObject jsonObject, String optionName) {
		Optional<JsonElement> opt = Optional.ofNullable(jsonObject.get(optionName));
		return opt.map(j -> j.getAsJsonArray()).orElse(new JsonArray(0));
	}

}