package org.elwiki.data.persist.json;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.elwiki.data.persist.json.deserialize.AclDeserializer;
import org.elwiki.data.persist.json.deserialize.PageAttachmentDeserializer;
import org.elwiki.data.persist.json.deserialize.PageContentDeserializer;
import org.elwiki.data.persist.json.deserialize.PagesStoreDeserializer;
import org.elwiki.data.persist.json.deserialize.WikiPageDeserializer;
import org.elwiki_data.Acl;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PagesStore;
import org.elwiki_data.WikiPage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonDeserialiser {

	public static void LoadData() throws IOException {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting() //@formatter:off
				.registerTypeAdapter(PagesStore.class, new PagesStoreDeserializer())
				.registerTypeAdapter(WikiPage.class, new WikiPageDeserializer())
				.registerTypeAdapter(PageContent.class, new PageContentDeserializer())
				.registerTypeAdapter(PageAttachment.class, new PageAttachmentDeserializer())
				.registerTypeAdapter(Acl.class, new AclDeserializer())
				.create(); //@formatter:on

		// Writer writer = new FileWriter(JsonConstants.FILE_NAME);

		try (Reader input = new FileReader(JsonConstants.FILE_NAME)) {
			gson.fromJson(input, PagesStore.class);
		}
	}

}
