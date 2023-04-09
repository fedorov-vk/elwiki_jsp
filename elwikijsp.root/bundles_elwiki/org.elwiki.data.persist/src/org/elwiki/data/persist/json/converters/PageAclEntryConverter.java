package org.elwiki.data.persist.json.converters;

import java.lang.reflect.Type;

import org.eclipse.emf.common.util.EList;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PageAclEntry;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PageAclEntryConverter extends DeserialiseStuff implements JsonSerializer<PageAclEntry>, JsonDeserializer<PageAclEntry> {

	private static final String IS_ALLOW = "isAllow";
	private static final String PERMISSION = "permission";
	private static final String ROLES = "roles";

	@Override
	public JsonElement serialize(PageAclEntry aclEntry, Type typeOfSrc, JsonSerializationContext arg2) {
		JsonObject result = new JsonObject();

		result.addProperty(IS_ALLOW, aclEntry.isAllow());
		result.addProperty(PERMISSION, aclEntry.getPermission());

		JsonArray roles = new JsonArray();
		result.add(ROLES, roles);
		for (String role : aclEntry.getRoles()) {
			roles.add(role);
		}

		return result;
	}

	@Override
	public PageAclEntry deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		PageAclEntry aclEntry = Elwiki_dataFactory.eINSTANCE.createPageAclEntry();

		boolean isAllow = getBoolean(jsonObject, IS_ALLOW);
		String permission = getString(jsonObject, PERMISSION);

		aclEntry.setAllow(isAllow);
		aclEntry.setPermission(permission);

		EList<String> roles = aclEntry.getRoles();
		for (JsonElement jsonRole : getArray(jsonObject, ROLES)) {
			roles.add(jsonRole.getAsString());
		}

		return aclEntry;
	}

}
