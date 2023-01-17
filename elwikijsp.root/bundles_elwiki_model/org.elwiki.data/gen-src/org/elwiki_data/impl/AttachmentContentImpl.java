/**
 */
package org.elwiki_data.impl;

import java.util.Date;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.elwiki_data.AttachmentContent;
import org.elwiki_data.Elwiki_dataPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attachment Content</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#getLastModifiedDate <em>Last Modified Date</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#getChangeNote <em>Change Note</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#getPlace <em>Place</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#getSize <em>Size</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AttachmentContentImpl#isCacheable <em>Cacheable</em>}</li>
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
	public short getVersion() {
		return (Short)eGet(Elwiki_dataPackage.Literals.IHISTORY_INFO__VERSION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setVersion(short newVersion) {
		eSet(Elwiki_dataPackage.Literals.IHISTORY_INFO__VERSION, newVersion);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Date getLastModifiedDate() {
		return (Date)eGet(Elwiki_dataPackage.Literals.IHISTORY_INFO__LAST_MODIFIED_DATE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLastModifiedDate(Date newLastModifiedDate) {
		eSet(Elwiki_dataPackage.Literals.IHISTORY_INFO__LAST_MODIFIED_DATE, newLastModifiedDate);
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
	public boolean isCacheable() {
		return (Boolean)eGet(Elwiki_dataPackage.Literals.ATTACHMENT_CONTENT__CACHEABLE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCacheable(boolean newCacheable) {
		eSet(Elwiki_dataPackage.Literals.ATTACHMENT_CONTENT__CACHEABLE, newCacheable);
	}

} //AttachmentContentImpl
