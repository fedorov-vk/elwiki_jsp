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
 *   <li>{@link org.elwiki_data.AttachmentContent#getPageAttachment <em>Page Attachment</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getAttachmentContent()
 * @model
 * @generated
 */
public interface AttachmentContent extends IHistoryInfo {
	/**
	 * Returns the value of the '<em><b>Place</b></em>' attribute.
	 * The default value is <code>""</code>.
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
	 * @model default=""
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
	 * The default value is <code>"0"</code>.
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
	 * @model default="0"
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
	 * Returns the value of the '<em><b>Page Attachment</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.elwiki_data.PageAttachment#getAttachContents <em>Attach Contents</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Page Attachment</em>' container reference.
	 * @see #setPageAttachment(PageAttachment)
	 * @see org.elwiki_data.Elwiki_dataPackage#getAttachmentContent_PageAttachment()
	 * @see org.elwiki_data.PageAttachment#getAttachContents
	 * @model opposite="attachContents" transient="false"
	 * @generated
	 */
	PageAttachment getPageAttachment();

	/**
	 * Sets the value of the '{@link org.elwiki_data.AttachmentContent#getPageAttachment <em>Page Attachment</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Page Attachment</em>' container reference.
	 * @see #getPageAttachment()
	 * @generated
	 */
	void setPageAttachment(PageAttachment value);

} // AttachmentContent
