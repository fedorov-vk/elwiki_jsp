package org.elwiki.preferences;

import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.scripting.ClientListener;

/**
 * Defines JS action, executed on RAP client.
 * 
 * @author vfedorov
 */
public class MyClientListener extends ClientListener {

	private static final long serialVersionUID = 3179193035513048008L;

	public static MyClientListener getInstance() {
		return SingletonUtil.getSessionInstance(MyClientListener.class);
	}

	public MyClientListener() {
		//@formatter:off
		super("var handleEvent = function( event ){\n"
			+ " window.top.postMessage('hello', '*'); \n"
			+ "};"
		);
		//@formatter:on
	}
}
