package org.elwiki.preferences;

import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.scripting.ClientListener;

public class MyClientListener extends ClientListener {

	private static final long serialVersionUID = 3179193035513048008L;

	public static MyClientListener getInstance() {
		return SingletonUtil.getSessionInstance(MyClientListener.class);
	}

	public MyClientListener() {
		super(getText());
	}

	private static String getText() {
		String script;
		/*
		script = "var handleEvent = function( event ){\n"
				+ "  event.widget.setText( \"Hello World!\" );\n"
		+ "  console.log( \"Hello World!\" );\n"
				+ "};";
		*/
		
		/* WORKS:
		script = "var handleEvent = function( event ){\n"
				+ " window.top.postMessage('hello', '*'); \n"
				+ "};";
		 */
		
		script = "var handleEvent = function( event ){\n"
				+ " window.top.postMessage('hello', '*'); \n"
				+ "};";
		// :FVK: return ResourceLoaderUtil.readTextContent("MyScript.js");
		return script;
	}

}
