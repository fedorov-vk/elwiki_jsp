package org.elwiki.api.component;

import org.apache.wiki.api.exceptions.WikiException;

/**
 * Manager's interface for Engine's components. <br/>
 * It can be initialized.
 */
public interface WikiManager {

	/**
	 * <p>
	 * Initializes this component. Note that other wiki components are not fully initialized at this
	 * point, so don't do anything fancy here - use lazy init, if you have to.<br/>
	 * &nbsp;
	 * </p>
	 *
	 * @throws WikiException if an exception occurs while initializing the component.
	 */
	void initialize() throws WikiException;

}
