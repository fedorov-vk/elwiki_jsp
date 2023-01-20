package org.elwiki.data.persist.json.converters;

import java.lang.reflect.Type;
import java.security.Permission;

import org.elwiki_data.Acl;
import org.elwiki_data.AclEntry;
import org.elwiki_data.Elwiki_dataFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class AclConverter extends DeserialiseStuff implements JsonSerializer<Acl>, JsonDeserializer<Acl> {

	@Override
	public JsonElement serialize(Acl acl, Type typeOfSrc, JsonSerializationContext arg2) {
		JsonObject result = new JsonObject();

		JsonArray entries = new JsonArray();
		result.add("entries", entries);
		for (AclEntry aclEntry : acl.getAclEntries()) {
			JsonObject jsonEntry = new JsonObject();
			jsonEntry.addProperty("principal", aclEntry.getPrincipal().toString());

			JsonArray jsonPermissions = new JsonArray();
			jsonEntry.add("permissions", jsonPermissions);
			for (Permission permission : aclEntry.getPermission()) {
				JsonObject jsonPermission = new JsonObject();
				jsonPermission.addProperty("permission", permission.toString());
				jsonPermissions.add(jsonPermission);
			}
			entries.add(jsonEntry);
		}

		return result;
	}

	@SuppressWarnings("unused")
	@Override
	public Acl deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		Acl acl = Elwiki_dataFactory.eINSTANCE.createAcl();

		for (JsonElement jsonEntry : getArray(jsonObject, "entries")) {
			AclEntry aclEntry = Elwiki_dataFactory.eINSTANCE.createAclEntry();
			JsonObject jsonEntryObj = jsonEntry.getAsJsonObject();
			String principal = getString(jsonEntryObj, "principal");
			//TODO: aclEntry.setPrincipal();

			for (JsonElement jsonPermission : getArray(jsonEntryObj, "permissions")) {
				//TODO:
			}
		}

		return acl;
	}

}
