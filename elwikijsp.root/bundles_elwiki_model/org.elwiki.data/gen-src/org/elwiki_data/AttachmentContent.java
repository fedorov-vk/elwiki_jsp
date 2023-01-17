/**
 */
package org.elwiki_data;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attachment Content</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.AttachmentContent#getPlace <em>Place</em>}</li>
 *   <li>{@link org.elwiki_data.AttachmentContent#getSize <em>Size</em>}</li>
 *   <li>{@link org.elwiki_data.AttachmentContent#isCacheable <em>Cacheable</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getAttachmentContent()
 * @model
 * @generated
 */
public interface AttachmentContent extends IHistoryInfo {
	/**
	 * Returns the value of the '<em><b>Place</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Place</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Place</em>' attribute.
	 * @see #setPlace(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getAttachmentContent_Place()
	 * @model
	 * @generated
	 */
	String getPlace();

	/**
	 * Sets the value of the '{@link org.elwiki_data.AttachmentContent#getPlace <em>Place</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Place</em>' attribute.
	 * @see #getPlace()
	 * @generated
	 */
	void setPlace(String value);

	/**
	 * Returns the value of the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Size</em>' attribute isn't clear,
	 *  there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Size</em>' attribute.
	 * @see #setSize(long)
	 * @see org.elwiki_data.Elwiki_dataPackage#getAttachmentContent_Size()
	 * @model
	 * @generated
	 */
	long getSize();

	/**
	 * Sets the value of the '{@link org.elwiki_data.AttachmentContent#getSize <em>Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Size</em>' attribute.
	 * @see #getSize()
	 * @generated
	 */
	void setSize(long value);

	/**
	 * Returns the value of the '<em><b>Cacheable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Cacheable</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Cacheable</em>' attribute.
	 * @see #setCacheable(boolean)
	 * @see org.elwiki_data.Elwiki_dataPackage#getAttachmentContent_Cacheable()
	 * @model
	 * @generated
	 */
	boolean isCacheable();

	/**
	 * Sets the value of the '{@link org.elwiki_data.AttachmentContent#isCacheable <em>Cacheable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Cacheable</em>' attribute.
	 * @see #isCacheable()
	 * @generated
	 */
	void setCacheable(boolean value);

} // AttachmentContent
