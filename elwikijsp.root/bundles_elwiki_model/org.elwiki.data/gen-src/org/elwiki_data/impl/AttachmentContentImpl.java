/**
 */
package org.elwiki_data.impl;

import java.util.Date;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.elwiki_data.AttachmentContent;
import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.PageAttachment;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attachment Content</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#getCreationDate <em>Creation Date</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#getChangeNote <em>Change Note</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#getPlace <em>Place</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#getSize <em>Size</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#getPageAttachment <em>Page Attachment</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AttachmentContentImpl extends CDOObjectImpl implements AttachmentContent {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AttachmentContentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Elwiki_dataPackage.Literals.ATTACHMENT_CONTENT;
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
		return (Integer)eGet(Elwiki_dataPackage.Literals.IHISTORY_INFO__VERSION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setVersion(int newVersion) {
		eSet(Elwiki_dataPackage.Literals.IHISTORY_INFO__VERSION, newVersion);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Date getCreationDate() {
		return (Date)eGet(Elwiki_dataPackage.Literals.IHISTORY_INFO__CREATION_DATE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCreationDate(Date newCreationDate) {
		eSet(Elwiki_dataPackage.Literals.IHISTORY_INFO__CREATION_DATE, newCreationDate);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAuthor() {
		return (String)eGet(Elwiki_dataPackage.Literals.IHISTORY_INFO__AUTHOR, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAuthor(String newAuthor) {
		eSet(Elwiki_dataPackage.Literals.IHISTORY_INFO__AUTHOR, newAuthor);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getChangeNote() {
		return (String)eGet(Elwiki_dataPackage.Literals.IHISTORY_INFO__CHANGE_NOTE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setChangeNote(String newChangeNote) {
		eSet(Elwiki_dataPackage.Literals.IHISTORY_INFO__CHANGE_NOTE, newChangeNote);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPlace() {
		return (String)eGet(Elwiki_dataPackage.Literals.ATTACHMENT_CONTENT__PLACE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPlace(String newPlace) {
		eSet(Elwiki_dataPackage.Literals.ATTACHMENT_CONTENT__PLACE, newPlace);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getSize() {
		return (Long)eGet(Elwiki_dataPackage.Literals.ATTACHMENT_CONTENT__SIZE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSize(long newSize) {
		eSet(Elwiki_dataPackage.Literals.ATTACHMENT_CONTENT__SIZE, newSize);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PageAttachment getPageAttachment() {
		return (PageAttachment)eGet(Elwiki_dataPackage.Literals.ATTACHMENT_CONTENT__PAGE_ATTACHMENT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPageAttachment(PageAttachment newPageAttachment) {
		eSet(Elwiki_dataPackage.Literals.ATTACHMENT_CONTENT__PAGE_ATTACHMENT, newPageAttachment);
	}

} //AttachmentContentImpl
