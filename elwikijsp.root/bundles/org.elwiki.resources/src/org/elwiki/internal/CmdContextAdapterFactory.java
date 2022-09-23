package org.elwiki.internal;

import org.apache.wiki.api.core.ContextEnum;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Adapter factory converting URI to ContextEnum.<br/>
 * URI without a leading '/' is examined.
 *
 * @author v.fedorov
 */
public class CmdContextAdapterFactory implements IAdapterFactory {

	// use a static final field so that the adapterList is only instantiated once.
	private static final Class<?>[] adapterList = new Class<?>[] { ContextEnum.class };

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adapterType == ContextEnum.class && adaptableObject instanceof String) {
			return adapterType.cast(getContext((String) adaptableObject));
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return adapterList;
	}

	/**
	 * Get WikiContext according given URI. If URI is not determined as known - then returns
	 * default, ContextEnum.PAGE_VIEW.
	 * 
	 * @param adaptableObject
	 * @return Wiki context.
	 */
	private Object getContext(String adaptableObject) {
		for (ContextEnum context : ContextEnum.values()) {
			if (adaptableObject.equals(context.getUri())) {
				return context;
			}
		}

		return ContextEnum.PAGE_VIEW;
	}
}
