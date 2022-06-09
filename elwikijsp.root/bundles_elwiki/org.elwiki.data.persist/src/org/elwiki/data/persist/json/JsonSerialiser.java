package org.elwiki.data.persist.json;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.eclipse.emf.common.util.EList;
import org.elwiki.data.persist.internal.PluginActivator;
import org.elwiki.data.persist.json.serialize.AclSerializer;
import org.elwiki.data.persist.json.serialize.PageAttachmentSerializer;
import org.elwiki.data.persist.json.serialize.PageContentSerializer;
import org.elwiki.data.persist.json.serialize.PagesStoreSerializer;
import org.elwiki.data.persist.json.serialize.WikiPageSerializer;
import org.elwiki_data.Acl;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PagesStore;
import org.elwiki_data.WikiPage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonSerialiser {

	@SuppressWarnings("unused")
	public static void SaveData() throws IOException {
		// Gson gson = new
		// GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
		Gson gson = new GsonBuilder()
				.setPrettyPrinting() //@formatter:off
				.registerTypeAdapter(PagesStore.class, new PagesStoreSerializer())
				.registerTypeAdapter(WikiPage.class, new WikiPageSerializer())
				.registerTypeAdapter(PageContent.class, new PageContentSerializer())
				.registerTypeAdapter(PageAttachment.class, new PageAttachmentSerializer())
				.registerTypeAdapter(Acl.class, new AclSerializer())
				.create(); //@formatter:on

		PagesStore pagesStore = PluginActivator.getDefault().getDataStore().getPagesStore();
		EList<WikiPage> pages = pagesStore.getWikipages();
		// WikiPage wikiPage = pages.get(0);

		try (Writer writer = new FileWriter(JsonConstants.FILE_NAME)) {
			gson.toJson(pagesStore, PagesStore.class, writer);
			writer.close();
		}

		/*
		FileOutputStream outputStream;
		outputStream = new FileOutputStream(FILE_NAME);
		BufferedWriter bufferedWriter = new BufferedWriter(
				new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)); // "UTF-8" gson.toJson(wikiPage, bufferedWriter);
		bufferedWriter.close();
		*/
	}
}
