package org.elwiki.manager.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import dwedata.DB;

/**
 * Импортирование XML файла JSPwiki. (созданного архиватором JSPwiki).<br/>
 * Например, файл "EMF.xml" - размещается в каталоге "archive" рабочей области ElWiki.
 * 
 * @author v.fedorov
 */
public class ImportJspWikiData {

	private IPath workspacePath;

	private DB db;

	static final String JspWiki_NAME = "JavaPatterns"; // имя JSPwiki.

	static final String SUBDIR = "archive";
	static final String ATTACHMENT_SUBDIR = "Wiki_Attach-";

	public ImportJspWikiData(IPath workspacePath) {
		this.workspacePath = workspacePath;
	}

	public DB getDb() {
		return db;
	}

	public void readXmlData() {
		IPath fileName = workspacePath.append(SUBDIR).append(JspWiki_NAME).addFileExtension("xml");
		File f = fileName.toFile();
		if (!f.isFile()) {
			return;
		}
		URI uri = URI.createFileURI(fileName.toPortableString());
		Resource resource = new XMLResourceImpl(uri);

		try {
			resource.load(null);
			db = (DB) resource.getContents().get(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public InputStream importAttachmentData(String fileName) throws FileNotFoundException {
		IPath fileName1 = workspacePath.append(SUBDIR).append(ATTACHMENT_SUBDIR+JspWiki_NAME).append(fileName);
		File file = fileName1.toFile();
		InputStream in = new FileInputStream(file);

		return in;
	}

}
