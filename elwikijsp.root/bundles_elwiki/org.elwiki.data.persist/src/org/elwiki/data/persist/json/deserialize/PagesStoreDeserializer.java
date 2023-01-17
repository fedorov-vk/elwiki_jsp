package org.elwiki.data.persist.json.deserialize;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;
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

public class PagesStoreDeserializer extends DeserialiseStuff implements JsonDeserializer<PagesStore> {

	private static final Logger log = Logger.getLogger(PagesStoreDeserializer.class);
	
	@Override
	public PagesStore deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		String mainPageId = getString(jsonObject, "mainPageId");
		String nextPageId = getString(jsonObject, "nextPageId");
		JsonArray jsonPages = getArray(jsonObject, "pages");

		PagesStore pagesStore = PluginActivator.getDefault().getDataStore().getPagesStore();
		CDOTransaction transaction = PluginActivator.getDefault().getDataStore().getTransactionCDO();
		pagesStore = transaction.getObject(pagesStore);

		pagesStore.setMainPageId(mainPageId);
		pagesStore.setNextPageId(nextPageId);

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
