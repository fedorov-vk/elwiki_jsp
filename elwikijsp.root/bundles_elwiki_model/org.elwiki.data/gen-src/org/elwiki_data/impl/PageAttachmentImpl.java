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
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getLastVersion <em>Last Version</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getWikipage <em>Wikipage</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getAttachContents <em>Attach Contents</em>}</li>
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
	public String getId() {
		return (String)eGet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setId(String newId) {
		eSet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__ID, newId);
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
	public int getLastVersion() {
		return (Integer)eGet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__LAST_VERSION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLastVersion(int newLastVersion) {
		eSet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__LAST_VERSION, newLastVersion);
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
	public AttachmentContent forLastContent() {
		AttachmentContent result = null;
		int version = 0;
		EList<AttachmentContent> attachContents = getAttachContents();
		for (AttachmentContent attContent : attachContents) {
			int contentVersion = attContent.getVersion();
			if (version < contentVersion) {
				version = contentVersion;
				result = attContent;
			}
		}
		
		if (result == null)
			return Elwiki_dataFactoryImpl.eINSTANCE.createAttachmentContent();
		
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AttachmentContent forVersionContent(final int desiredVersion) {
		AttachmentContent result = null;
		EList<AttachmentContent> attachContents = getAttachContents();
		for (AttachmentContent attContent : attachContents) {
			if (desiredVersion == attContent.getVersion()) {
				result = attContent;
				break;
			}
		}
		
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
			case Elwiki_dataPackage.PAGE_ATTACHMENT___FOR_VERSION_CONTENT__INT:
				return forVersionContent((Integer)arguments.get(0));
		}
		return super.eInvoke(operationID, arguments);
	}

} //PageAttachmentImpl
