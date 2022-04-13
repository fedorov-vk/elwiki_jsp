package org.elwiki;

public interface IWikiConstants {

	/**
	 * Статус аутентификации.
	 */
	public enum StatusType {
		/** An anonymous user's session status. */
		ANONYMOUS("anonymous"),
		/** An asserted user's session status. */
		ASSERTED("asserted"),
		/** An authenticated user's session status. */
		AUTHENTICATED("authenticated");

		private String id;

		private StatusType(String id) {
			this.id = id;
		}

		public String getId() {
			return this.id;
		}
	}

}
