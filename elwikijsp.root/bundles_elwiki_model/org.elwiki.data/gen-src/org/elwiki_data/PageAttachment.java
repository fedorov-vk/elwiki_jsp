/**
 */
package org.elwiki_data;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Page Attachment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.PageAttachment#getId <em>Id</em>}</li>
 *   <li>{@link org.elwiki_data.PageAttachment#getName <em>Name</em>}</li>
 *   <li>{@link org.elwiki_data.PageAttachment#getLastVersion <em>Last Version</em>}</li>
 *   <li>{@link org.elwiki_data.PageAttachment#getWikipage <em>Wikipage</em>}</li>
 *   <li>{@link org.elwiki_data.PageAttachment#getAttachContents <em>Attach Contents</em>}</li>
 *   <li>{@link org.elwiki_data.PageAttachment#getAttachmentContent <em>Attachment Content</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getPageAttachment()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface PageAttachment extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPageAttachment_Id()
	 * @model
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PageAttachment#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPageAttachment_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PageAttachment#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Wikipage</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.elwiki_data.WikiPage#getAttachments <em>Attachments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Wikipage</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Wikipage</em>' container reference.
	 * @see #setWikipage(WikiPage)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPageAttachment_Wikipage()
	 * @see org.elwiki_data.WikiPage#getAttachments
	 * @model opposite="attachments" transient="false"
	 * @generated
	 */
	WikiPage getWikipage();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PageAttachment#getWikipage <em>Wikipage</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wikipage</em>' container reference.
	 * @see #getWikipage()
	 * @generated
	 */
	void setWikipage(WikiPage value);

	/**
	 * Returns the value of the '<em><b>Attach Contents</b></em>' containment reference list.
	 * The list contents are of type {@link org.elwiki_data.AttachmentContent}.
	 * It is bidirectional and its opposite is '{@link org.elwiki_data.AttachmentContent#getPageAttachment <em>Page Attachment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attach Contents</em>' containment reference list.
	 * @see org.elwiki_data.Elwiki_dataPackage#getPageAttachment_AttachContents()
	 * @see org.elwiki_data.AttachmentContent#getPageAttachment
	 * @model opposite="pageAttachment" containment="true"
	 * @generated
	 */
	EList<AttachmentContent> getAttachContents();

	/**
	 * Returns the value of the '<em><b>Last Version</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Last Version</em>' attribute.
	 * @see #setLastVersion(int)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPageAttachment_LastVersion()
	 * @model default="0"
	 * @generated
	 */
	int getLastVersion();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PageAttachment#getLastVersion <em>Last Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Last Version</em>' attribute.
	 * @see #getLastVersion()
	 * @generated
	 */
	void setLastVersion(int value);

	/**
	 * Returns the value of the '<em><b>Attachment Content</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attachment Content</em>' reference.
	 * @see #setAttachmentContent(AttachmentContent)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPageAttachment_AttachmentContent()
	 * @model transient="true"
	 * @generated
	 */
	AttachmentContent getAttachmentContent();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PageAttachment#getAttachmentContent <em>Attachment Content</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Attachment Content</em>' reference.
	 * @see #getAttachmentContent()
	 * @generated
	 */
	void setAttachmentContent(AttachmentContent value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns last item of attachContents reference. And set reference of AttachmentContent.\n</br>Can be NULL.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	AttachmentContent forLastContent();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns item of attachContents reference for required version. And set reference of AttachmentContent.\n</br>Can be NULL.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	AttachmentContent forVersionContent(int desiredVersion);

} // PageAttachment
