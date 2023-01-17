package org.elwiki.data.persist.json.deserialize;

import java.lang.reflect.Type;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.elwiki_data.Acl;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.WikiPage;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class WikiPageDeserializer extends DeserialiseStuff implements JsonDeserializer<WikiPage> {

	@SuppressWarnings("unused")
	@Override
	public WikiPage deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		WikiPage wikiPage = Elwiki_dataFactory.eINSTANCE.createWikiPage();
		
		String id = getString(jsonObject, "id");
		String name = getString(jsonObject, "name");
		String description = getString(jsonObject, "description");
		String alias = getString(jsonObject, "alias");
		String redirect = getString(jsonObject, "redirect");
		int viewCount = getInt(jsonObject, "viewCount");
		String wiki = getString(jsonObject, "wiki");

		wikiPage.setId(id);
		wikiPage.setName(name);
		wikiPage.setDescription(description);
		wikiPage.setAlias(alias);
		wikiPage.setRedirect(redirect);
		wikiPage.setViewCount(viewCount);
		wikiPage.setWiki(wiki);
		
		EList<PageReference> pageReferences = wikiPage.getPageReferences();
		JsonArray jsonReferences = getArray(jsonObject, "references");
		for (JsonElement jsonRef : jsonReferences) {
			PageReference pageReference = Elwiki_dataFactory.eINSTANCE.createPageReference();
			pageReference.setPageId(jsonRef.getAsString());
			pageReferences.add(pageReference);
		}

		EList<WikiPage> children = wikiPage.getChildren();
		for (JsonElement jsonPage : getArray(jsonObject, "children")) {
			WikiPage childWikiPage = context.deserialize(jsonPage, WikiPage.class);
			children.add(childWikiPage);
		}

		for (JsonElement jsonAttrib : getArray(jsonObject, "attributes")) {
			for (Map.Entry<String, JsonElement> entry : jsonAttrib.getAsJsonObject().entrySet()) {
				System.out.println(entry.getKey() + ":" + entry.getValue());
			}
			//TODO: JSON restore attributes.
		}
		
		EList<PageContent> contents = wikiPage.getPageContents();
		for (JsonElement jsonContent : getArray(jsonObject, "contents")) {
			PageContent pageContent = context.deserialize(jsonContent, PageContent.class);
			contents.add(pageContent);
		}

		EList<PageAttachment> attachments = wikiPage.getAttachments();
		for (JsonElement jsonAttachment : getArray(jsonObject, "attachments")) {
			PageAttachment pageAttachment = context.deserialize(jsonAttachment, PageAttachment.class);
			if (pageAttachment != null) {
				attachments.add(pageAttachment);
			}
		}

		if(jsonObject.has("acl")) {
			Acl acl = context.deserialize(jsonObject.get("acl"), Acl.class);
			wikiPage.setAcl(acl);
		}

		return wikiPage;
	}

}
