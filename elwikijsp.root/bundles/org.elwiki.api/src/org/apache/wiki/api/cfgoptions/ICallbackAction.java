package org.apache.wiki.api.cfgoptions;

public interface ICallbackAction {

	/**
	 * Returns ID for JS scope this item.
	 *
	 * @return
	 */
	String getId();

	void callBack(String string);

}
