package org.elwiki.data.persist.json.serialize;

import java.lang.reflect.Type;
import java.security.Permission;

import org.elwiki_data.Acl;
import org.elwiki_data.AclEntry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class AclSerializer implements JsonSerializer<Acl> {

	@Override
	public JsonElement serialize(Acl acl, Type typeOfSrc, JsonSerializationContext arg2) {
		JsonObject result = new JsonObject();

		JsonArray entries = new JsonArray();
		result.add("entries", entries);
		for ( AclEntry aclEntry : acl.getAclEntries()) {
			JsonObject jsonEntry = new JsonObject();
			jsonEntry.addProperty("principal", aclEntry.getPrincipal().toString());
			
			JsonArray jsonPermissions = new JsonArray();
			jsonEntry.add("permissions", jsonPermissions);
			for(Permission permission : aclEntry.getPermission()) {
				JsonObject jsonPermission = new JsonObject();
				jsonPermission.addProperty("permission", permission.toString());
				jsonPermissions.add(jsonPermission);
			}
			entries.add(jsonEntry);
		}
		
		return result;
	}

}
