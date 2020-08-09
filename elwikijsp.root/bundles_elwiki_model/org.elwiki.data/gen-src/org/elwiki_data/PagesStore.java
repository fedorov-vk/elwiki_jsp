/**
 */
package org.elwiki_data;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Pages Store</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.PagesStore#getWikipages <em>Wikipages</em>}</li>
 *   <li>{@link org.elwiki_data.PagesStore#getMainPageId <em>Main Page Id</em>}</li>
 *   <li>{@link org.elwiki_data.PagesStore#getNextPageId <em>Next Page Id</em>}</li>
 *   <li>{@link org.elwiki_data.PagesStore#getNextAttachId <em>Next Attach Id</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getPagesStore()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface PagesStore extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Wikipages</b></em>' containment reference list.
	 * The list contents are of type {@link org.elwiki_data.WikiPage}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Wikipages</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Wikipages</em>' containment reference list.
	 * @see org.elwiki_data.Elwiki_dataPackage#getPagesStore_Wikipages()
	 * @model containment="true"
	 * @generated
	 */
	EList<WikiPage> getWikipages();

	/**
	 * Returns the value of the '<em><b>Main Page Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Main Page Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Main Page Id</em>' attribute.
	 * @see #setMainPageId(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPagesStore_MainPageId()
	 * @model
	 * @generated
	 */
	String getMainPageId();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PagesStore#getMainPageId <em>Main Page Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Main Page Id</em>' attribute.
	 * @see #getMainPageId()
	 * @generated
	 */
	void setMainPageId(String value);

	/**
	 * Returns the value of the '<em><b>Next Page Id</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Next Page Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Next Page Id</em>' attribute.
	 * @see #setNextPageId(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPagesStore_NextPageId()
	 * @model default=""
	 * @generated
	 */
	String getNextPageId();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PagesStore#getNextPageId <em>Next Page Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Next Page Id</em>' attribute.
	 * @see #getNextPageId()
	 * @generated
	 */
	void setNextPageId(String value);

	/**
	 * Returns the value of the '<em><b>Next Attach Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Next Attach Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Next Attach Id</em>' attribute.
	 * @see #setNextAttachId(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPagesStore_NextAttachId()
	 * @model
	 * @generated
	 */
	String getNextAttachId();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PagesStore#getNextAttachId <em>Next Attach Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Next Attach Id</em>' attribute.
	 * @see #getNextAttachId()
	 * @generated
	 */
	void setNextAttachId(String value);

} // PagesStore
