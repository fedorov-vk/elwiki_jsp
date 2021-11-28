/**
 */
package org.elwiki_data.impl;

import java.util.Date;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

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
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getLastModify <em>Last Modify</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getChangeNote <em>Change Note</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#isCacheable <em>Cacheable</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getWikipage <em>Wikipage</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getPlace <em>Place</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAttachmentImpl#getSize <em>Size</em>}</li>
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
	public int getVersion() {
		return (Integer)eGet(Elwiki_dataPackage.Literals.IMODIFY_INFO__VERSION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setVersion(int newVersion) {
		eSet(Elwiki_dataPackage.Literals.IMODIFY_INFO__VERSION, newVersion);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Date getLastModify() {
		return (Date)eGet(Elwiki_dataPackage.Literals.IMODIFY_INFO__LAST_MODIFY, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLastModify(Date newLastModify) {
		eSet(Elwiki_dataPackage.Literals.IMODIFY_INFO__LAST_MODIFY, newLastModify);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAuthor() {
		return (String)eGet(Elwiki_dataPackage.Literals.IMODIFY_INFO__AUTHOR, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAuthor(String newAuthor) {
		eSet(Elwiki_dataPackage.Literals.IMODIFY_INFO__AUTHOR, newAuthor);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getChangeNote() {
		return (String)eGet(Elwiki_dataPackage.Literals.IMODIFY_INFO__CHANGE_NOTE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setChangeNote(String newChangeNote) {
		eSet(Elwiki_dataPackage.Literals.IMODIFY_INFO__CHANGE_NOTE, newChangeNote);
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
	public boolean isCacheable() {
		return (Boolean)eGet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__CACHEABLE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCacheable(boolean newCacheable) {
		eSet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__CACHEABLE, newCacheable);
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
	@Override
	public String getPlace() {
		return (String)eGet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__PLACE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPlace(String newPlace) {
		eSet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__PLACE, newPlace);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getSize() {
		return (Long)eGet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__SIZE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSize(long newSize) {
		eSet(Elwiki_dataPackage.Literals.PAGE_ATTACHMENT__SIZE, newSize);
	}

} //PageAttachmentImpl
