package org.elwiki.data.persist.json.converters;

import java.util.Date;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DeserialiseStuff {

	public DeserialiseStuff() {
		super();
	}

	protected String getString(JsonObject jsonObject, String optionName) {
		Optional<JsonElement> opt = Optional.ofNullable(jsonObject.get(optionName));
		return opt.map(j -> j.getAsString()).orElse("");
	}
	
	protected Date getDate(JsonObject jsonObject, String optionName, JsonDeserializationContext context) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(getString(jsonObject,optionName));
		return context.deserialize(jsonPrimitive, Date.class);
	}
	

	protected short getShort(JsonObject jsonObject, String optionName) {
		Optional<JsonElement> opt = Optional.ofNullable(jsonObject.get(optionName));
		return opt.map(j -> j.getAsShort()).orElse((short) 0);
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