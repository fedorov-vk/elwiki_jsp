package org.elwiki.rss.internal.options;

public interface ICallbackAction {

	/**
	 * Returns ID for JS scope this item.
	 *
	 * @return
	 */
	String getId();

	void callBack(String string);

}
