package org.elwiki.data.persist.json.converters;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateConverter implements JsonSerializer<Date>, JsonDeserializer<Date> {

	private static String PATTERN = "dd MMM yyyy HH:mm:ss.SSS ZZ";
	private final DateFormat dateFormatter;

	public DateConverter() {
		super();
		this.dateFormatter = new SimpleDateFormat(PATTERN, Locale.UK);
		this.dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public JsonElement serialize(Date date, Type typeOfSrc, JsonSerializationContext context) {
		JsonPrimitive result = null;

		if (date != null) {
			result = new JsonPrimitive(this.dateFormatter.format(date));
		}

		return result;
	}

	@Override
	public Date deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
//			JsonObject jsonObject = jsonElement.getAsJsonObject();
//			
//			Optional<JsonElement> opt = Optional.ofNullable(jsonObject.get("creationDate"));
			String string = "1";
			//= opt.map(j -> j.getAsString()).orElse("");
		string = jsonElement.getAsString();
			
			
			//Object string = context.deserialize(jsonElement, String.class);
			Date dateObject = this.dateFormatter.parse((String)string);
            return dateObject; //formatter.parse(json?.asString)
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
		// TODO Auto-generated method stub
		return null;
	}

}
