package org.elwiki.data.persist.json.converters;

import java.lang.reflect.Type;

import org.eclipse.emf.common.util.EList;
import org.elwiki_data.AclInfo;
import org.elwiki_data.Elwiki_dataFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class AclInfoConverter extends DeserialiseStuff implements JsonSerializer<AclInfo>, JsonDeserializer<AclInfo> {

	private static final String IS_ALLOW = "isAllow";
	private static final String PERMISSION = "permission";
	private static final String ROLES = "roles";

	@Override
	public JsonElement serialize(AclInfo aclInfo, Type typeOfSrc, JsonSerializationContext arg2) {
		JsonObject result = new JsonObject();

		result.addProperty(IS_ALLOW, aclInfo.isAllow());
		result.addProperty(PERMISSION, aclInfo.getPermission());

		JsonArray roles = new JsonArray();
		result.add(ROLES, roles);
		for (String role : aclInfo.getRoles()) {
			roles.add(role);
		}

		return result;
	}

	@Override
	public AclInfo deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		AclInfo aclInfo = Elwiki_dataFactory.eINSTANCE.createAclInfo();

		boolean isAllow = getBoolean(jsonObject, IS_ALLOW);
		String permission = getString(jsonObject, PERMISSION);

		aclInfo.setAllow(isAllow);
		aclInfo.setPermission(permission);

		EList<String> roles = aclInfo.getRoles();
		for (JsonElement jsonRole : getArray(jsonObject, ROLES)) {
			roles.add(jsonRole.getAsString());
		}

		return aclInfo;
	}

}
