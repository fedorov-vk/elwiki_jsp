package org.elwiki.test.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;

import org.apache.wiki.util.FileUtil;

public class SystemUtility {

	/**
	 * Makes a temporary file with some content, and returns a handle to it.
	 */
	public static File makeAttachmentFile() throws Exception {
		File tmpFile = File.createTempFile("test", "txt");
		tmpFile.deleteOnExit();

		FileWriter out = new FileWriter(tmpFile);

		FileUtil.copyContents(new StringReader("asdfa???dfzbvasdjkfbwfkUg783gqdwog"), out);

		out.close();

		return tmpFile;
	}

}