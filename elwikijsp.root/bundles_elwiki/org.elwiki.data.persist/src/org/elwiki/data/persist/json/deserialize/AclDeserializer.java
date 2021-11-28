package org.elwiki.data.persist.json.deserialize;

import java.lang.reflect.Type;

import org.elwiki_data.Acl;
import org.elwiki_data.AclEntry;
import org.elwiki_data.Elwiki_dataFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class AclDeserializer extends DeserialiseStuff implements JsonDeserializer<Acl> {

	@SuppressWarnings("unused")
	@Override
	public Acl deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		Acl acl = Elwiki_dataFactory.eINSTANCE.createAcl();
		
		for (JsonElement jsonEntry : getArray(jsonObject, "entries")) {
			AclEntry aclEntry = Elwiki_dataFactory.eINSTANCE.createAclEntry();
			JsonObject jsonEntryObj = jsonEntry.getAsJsonObject();
			String principal = getString(jsonEntryObj,"principal");
			//TODO: aclEntry.setPrincipal();
			
			for (JsonElement jsonPermission : getArray(jsonEntryObj, "permissions")) {
				//TODO:
			}
		}

		return acl;
	}

}
