/**
 */
package org.elwiki_data;

import java.lang.Cloneable;
import java.lang.Comparable;
import java.lang.Object;

import java.util.Date;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Wiki Page</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.WikiPage#getId <em>Id</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getName <em>Name</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getDescription <em>Description</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getAlias <em>Alias</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getRedirect <em>Redirect</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getViewCount <em>View Count</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getPagecontents <em>Pagecontents</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getAttachments <em>Attachments</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getWiki <em>Wiki</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getChildren <em>Children</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getParent <em>Parent</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getOldParents <em>Old Parents</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getPageReferences <em>Page References</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getTotalAttachment <em>Total Attachment</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getAcl <em>Acl</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#isWebLog <em>Web Log</em>}</li>
 *   <li>{@link org.elwiki_data.WikiPage#getAttributes <em>Attributes</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage()
 * @model superTypes="org.elwiki_data.Comparable org.elwiki_data.Cloneable"
 * @extends CDOObject
 * @generated
 */
public interface WikiPage extends CDOObject, Comparable, Cloneable {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * The default value is <code>""</code>.
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
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_Id()
	 * @model default=""
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.elwiki_data.WikiPage#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * The default value is <code>""</code>.
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
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_Name()
	 * @model default=""
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.elwiki_data.WikiPage#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.elwiki_data.WikiPage#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Alias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Alias</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Alias</em>' attribute.
	 * @see #setAlias(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_Alias()
	 * @model
	 * @generated
	 */
	String getAlias();

	/**
	 * Sets the value of the '{@link org.elwiki_data.WikiPage#getAlias <em>Alias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Alias</em>' attribute.
	 * @see #getAlias()
	 * @generated
	 */
	void setAlias(String value);

	/**
	 * Returns the value of the '<em><b>Redirect</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Redirect</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Redirect</em>' attribute.
	 * @see #setRedirect(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_Redirect()
	 * @model
	 * @generated
	 */
	String getRedirect();

	/**
	 * Sets the value of the '{@link org.elwiki_data.WikiPage#getRedirect <em>Redirect</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Redirect</em>' attribute.
	 * @see #getRedirect()
	 * @generated
	 */
	void setRedirect(String value);

	/**
	 * Returns the value of the '<em><b>View Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>View Count</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>View Count</em>' attribute.
	 * @see #setViewCount(int)
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_ViewCount()
	 * @model
	 * @generated
	 */
	int getViewCount();

	/**
	 * Sets the value of the '{@link org.elwiki_data.WikiPage#getViewCount <em>View Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>View Count</em>' attribute.
	 * @see #getViewCount()
	 * @generated
	 */
	void setViewCount(int value);

	/**
	 * Returns the value of the '<em><b>Pagecontents</b></em>' containment reference list.
	 * The list contents are of type {@link org.elwiki_data.PageContent}.
	 * It is bidirectional and its opposite is '{@link org.elwiki_data.PageContent#getWikipage <em>Wikipage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Pagecontents</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Pagecontents</em>' containment reference list.
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_Pagecontents()
	 * @see org.elwiki_data.PageContent#getWikipage
	 * @model opposite="wikipage" containment="true"
	 * @generated
	 */
	EList<PageContent> getPagecontents();

	/**
	 * Returns the value of the '<em><b>Attachments</b></em>' containment reference list.
	 * The list contents are of type {@link org.elwiki_data.PageAttachment}.
	 * It is bidirectional and its opposite is '{@link org.elwiki_data.PageAttachment#getWikipage <em>Wikipage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Attachments</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Attachments</em>' containment reference list.
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_Attachments()
	 * @see org.elwiki_data.PageAttachment#getWikipage
	 * @model opposite="wikipage" containment="true"
	 * @generated
	 */
	EList<PageAttachment> getAttachments();

	/**
	 * Returns the value of the '<em><b>Wiki</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Wiki</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Wiki</em>' attribute.
	 * @see #setWiki(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_Wiki()
	 * @model default=""
	 * @generated
	 */
	String getWiki();

	/**
	 * Sets the value of the '{@link org.elwiki_data.WikiPage#getWiki <em>Wiki</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wiki</em>' attribute.
	 * @see #getWiki()
	 * @generated
	 */
	void setWiki(String value);

	/**
	 * Returns the value of the '<em><b>Children</b></em>' containment reference list.
	 * The list contents are of type {@link org.elwiki_data.WikiPage}.
	 * It is bidirectional and its opposite is '{@link org.elwiki_data.WikiPage#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Children</em>' containment reference list.
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_Children()
	 * @see org.elwiki_data.WikiPage#getParent
	 * @model opposite="parent" containment="true"
	 * @generated
	 */
	EList<WikiPage> getChildren();

	/**
	 * Returns the value of the '<em><b>Parent</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.elwiki_data.WikiPage#getChildren <em>Children</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Parent</em>' container reference.
	 * @see #setParent(WikiPage)
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_Parent()
	 * @see org.elwiki_data.WikiPage#getChildren
	 * @model opposite="children" transient="false"
	 * @generated
	 */
	WikiPage getParent();

	/**
	 * Sets the value of the '{@link org.elwiki_data.WikiPage#getParent <em>Parent</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent</em>' container reference.
	 * @see #getParent()
	 * @generated
	 */
	void setParent(WikiPage value);

	/**
	 * Returns the value of the '<em><b>Old Parents</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Old Parents</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Old Parents</em>' attribute.
	 * @see #setOldParents(String[])
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_OldParents()
	 * @model dataType="org.elwiki_data.ArrayString"
	 * @generated
	 */
	String[] getOldParents();

	/**
	 * Sets the value of the '{@link org.elwiki_data.WikiPage#getOldParents <em>Old Parents</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Old Parents</em>' attribute.
	 * @see #getOldParents()
	 * @generated
	 */
	void setOldParents(String[] value);

	/**
	 * Returns the value of the '<em><b>Page References</b></em>' containment reference list.
	 * The list contents are of type {@link org.elwiki_data.PageReference}.
	 * It is bidirectional and its opposite is '{@link org.elwiki_data.PageReference#getWikipage <em>Wikipage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Page References</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Page References</em>' containment reference list.
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_PageReferences()
	 * @see org.elwiki_data.PageReference#getWikipage
	 * @model opposite="wikipage" containment="true"
	 * @generated
	 */
	EList<PageReference> getPageReferences();

	/**
	 * Returns the value of the '<em><b>Total Attachment</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Total Attachment</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Total Attachment</em>' attribute.
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_TotalAttachment()
	 * @model default="0" transient="true" changeable="false"
	 * @generated
	 */
	int getTotalAttachment();

	/**
	 * Returns the value of the '<em><b>Acl</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Acl</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Acl</em>' containment reference.
	 * @see #setAcl(Acl)
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_Acl()
	 * @model containment="true"
	 * @generated
	 */
	Acl getAcl();

	/**
	 * Sets the value of the '{@link org.elwiki_data.WikiPage#getAcl <em>Acl</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Acl</em>' containment reference.
	 * @see #getAcl()
	 * @generated
	 */
	void setAcl(Acl value);

	/**
	 * Returns the value of the '<em><b>Web Log</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Web Log</em>' attribute.
	 * @see #setWebLog(boolean)
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_WebLog()
	 * @model default="false"
	 * @generated
	 */
	boolean isWebLog();

	/**
	 * Sets the value of the '{@link org.elwiki_data.WikiPage#isWebLog <em>Web Log</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Web Log</em>' attribute.
	 * @see #isWebLog()
	 * @generated
	 */
	void setWebLog(boolean value);

	/**
	 * Returns the value of the '<em><b>Attributes</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.Object},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attributes</em>' map.
	 * @see org.elwiki_data.Elwiki_dataPackage#getWikiPage_Attributes()
	 * @model mapType="org.elwiki_data.StringToObjectMap&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EJavaObject&gt;"
	 * @generated
	 */
	EMap<String, Object> getAttributes();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model objType="org.elwiki_data.Object"
	 * @generated
	 */
	int compareTo(Object obj);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model type="org.elwiki_data.Object"
	 * @generated
	 */
	Object clone();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns: modify date of last item of pagecontents reference, or 12.02.1972.
	 * <!-- end-model-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	Date getLastModified();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns: author of last item of pagecontents reference, or empty string.
	 * <!-- end-model-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	String getAuthor();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns last item of pagecontents reference.
	 * </br>Can be NULL.
	 * <!-- end-model-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	PageContent getLastContent();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns version of last item of pagecontents reference.
	 * </br>Can be -1, if no content.
	 * <!-- end-model-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	int getVersion();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns required attribute by name.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	Object getAttribute(String name);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Save given attribute by name.
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void setAttribute(String name, Object value);

} // WikiPage
