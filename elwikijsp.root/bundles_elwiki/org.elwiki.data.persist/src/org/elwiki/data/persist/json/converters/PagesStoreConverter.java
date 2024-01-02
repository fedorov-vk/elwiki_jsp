package org.elwiki.data.persist.json.converters;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.apache.wiki.api.IStorageCdo;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.elwiki.data.persist.internal.PluginActivator;
import org.elwiki_data.PagesStore;
import org.elwiki_data.WikiPage;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PagesStoreConverter extends DeserialiseStuff
		implements JsonSerializer<PagesStore>, JsonDeserializer<PagesStore> {

	private static final Logger log = Logger.getLogger(PagesStoreConverter.class);

	private static String MAIN_PAGE_ID = "mainPageId";
	private static String NEXT_PAGE_ID = "nextPageId";
	private static String PAGES = "pages";
	private static String NEXT_ATTACHMENT_ID = "nextAttachmentId";
	
	@Override
	public JsonElement serialize(PagesStore pagesStore, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty(MAIN_PAGE_ID, pagesStore.getMainPageId());
		result.addProperty(NEXT_PAGE_ID, pagesStore.getNextPageId());
		result.addProperty(NEXT_ATTACHMENT_ID, pagesStore.getNextAttachmentId());

		JsonArray pages = new JsonArray();
		result.add(PAGES, pages);
		for (WikiPage page : pagesStore.getWikipages()) {
			pages.add(context.serialize(page, WikiPage.class));
		}

		return result;
	}

	@Override
	public PagesStore deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		String mainPageId = getString(jsonObject, MAIN_PAGE_ID);
		String nextPageId = getString(jsonObject, NEXT_PAGE_ID);
		String nextAttachmentId = getString(jsonObject, NEXT_ATTACHMENT_ID);
		JsonArray jsonPages = getArray(jsonObject, PAGES);

		IStorageCdo storageCdo = PluginActivator.getDefault().getService(IStorageCdo.class);
		PagesStore pagesStore = storageCdo.getPagesStore();
		CDOTransaction transaction = storageCdo.getTransactionCDO();
		pagesStore = transaction.getObject(pagesStore);

		pagesStore.setMainPageId(mainPageId);
		pagesStore.setNextPageId(nextPageId);
		pagesStore.setNextAttachmentId(nextAttachmentId);

		for (JsonElement page : jsonPages) {
			WikiPage wikiPage = context.deserialize(page, WikiPage.class);
			pagesStore.getWikipages().add(wikiPage);
		}

		try {
			transaction.commit();
		} catch (ConcurrentAccessException ex) {
			log.error("Transaction data conflict (intersection of parallel sessions) - the operation is canceled.");
			transaction.rollback();
		} catch (CommitException e) {
			log.error(e.getMessage());
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}

		return pagesStore;
	}
}
