/**
 */
package org.elwiki_data.impl;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.elwiki_data.AttachmentContent;
import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.WikiPage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Page Attachment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getWikipage <em>Wikipage</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getAttachContents <em>Attach Contents</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getLastVersion <em>Last Version</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getAttachmentContent <em>Attachment Content</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PageAttachmentImpl extends CDOObjectImpl implements PageAttachment {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PageAttachmentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Elwiki_dataPackage.Literals.PAGE_ATTACHMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected int eStaticFeatureCount() {
		return 0;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return (String)eGet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__NAME, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		eSet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__NAME, newName);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public WikiPage getWikipage() {
		return (WikiPage)eGet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__WIKIPAGE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWikipage(WikiPage newWikipage) {
		eSet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__WIKIPAGE, newWikipage);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EList<AttachmentContent> getAttachContents() {
		return (EList<AttachmentContent>)eGet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__ATTACH_CONTENTS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public short getLastVersion() {
		return (Short)eGet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__LAST_VERSION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLastVersion(short newLastVersion) {
		eSet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__LAST_VERSION, newLastVersion);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AttachmentContent getAttachmentContent() {
		return (AttachmentContent)eGet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__ATTACHMENT_CONTENT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAttachmentContent(AttachmentContent newAttachmentContent) {
		eSet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__ATTACHMENT_CONTENT, newAttachmentContent);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AttachmentContent forLastContent() {
		AttachmentContent result = null;
		short version = 0;
		EList<AttachmentContent> attachContents = getAttachContents();
		for (AttachmentContent attContent : attachContents) {
			short contentVersion = attContent.getVersion();
			if (version < contentVersion) {
				version = contentVersion;
				result = attContent;
			}
		}
		
		setAttachmentContent(result);
		
		return result;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AttachmentContent forVersionContent(final short desiredVersion) {
		AttachmentContent result = null;
		EList<AttachmentContent> attachContents = getAttachContents();
		for (AttachmentContent attContent : attachContents) {
			if (desiredVersion == attContent.getVersion()) {
				result = attContent;
				break;
			}
		}
		
		setAttachmentContent(result);
		
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case Elwiki_dataPackage.PAGE_ATTACHMENT___FOR_LAST_CONTENT:
				return forLastContent();
			case Elwiki_dataPackage.PAGE_ATTACHMENT___FOR_VERSION_CONTENT__SHORT:
				return forVersionContent((Short)arguments.get(0));
		}
		return super.eInvoke(operationID, arguments);
	}

} //PageAttachmentImpl
