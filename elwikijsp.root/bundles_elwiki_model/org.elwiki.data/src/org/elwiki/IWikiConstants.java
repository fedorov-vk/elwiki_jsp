package org.elwiki;

public interface IWikiConstants {

	/**
	 * Статус аутентификации.
	 */
	public enum StatusType {
		//@formatter:off
		ANONYMOUS("anonymous"), /** An anonymous user's session status. */
		ASSERTED("asserted"), /** An asserted user's session status. */
		AUTHENTICATED("authenticated"); /** An authenticated user's session status. */
		//@formatter:on

		@SuppressWarnings("unused")
		private String id;

		private StatusType(String id) {
			this.id = id;
		}
	}

}