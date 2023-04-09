/**
 */
package org.elwiki_data;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Page Acl Entry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.PageAclEntry#isAllow <em>Allow</em>}</li>
 *   <li>{@link org.elwiki_data.PageAclEntry#getPermission <em>Permission</em>}</li>
 *   <li>{@link org.elwiki_data.PageAclEntry#getRoles <em>Roles</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getPageAclEntry()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface PageAclEntry extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Allow</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Allow</em>' attribute.
	 * @see #setAllow(boolean)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPageAclEntry_Allow()
	 * @model default="true"
	 * @generated
	 */
	boolean isAllow();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PageAclEntry#isAllow <em>Allow</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Allow</em>' attribute.
	 * @see #isAllow()
	 * @generated
	 */
	void setAllow(boolean value);

	/**
	 * Returns the value of the '<em><b>Permission</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Permission</em>' attribute.
	 * @see #setPermission(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPageAclEntry_Permission()
	 * @model
	 * @generated
	 */
	String getPermission();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PageAclEntry#getPermission <em>Permission</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Permission</em>' attribute.
	 * @see #getPermission()
	 * @generated
	 */
	void setPermission(String value);

	/**
	 * Returns the value of the '<em><b>Roles</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Roles</em>' attribute list.
	 * @see org.elwiki_data.Elwiki_dataPackage#getPageAclEntry_Roles()
	 * @model
	 * @generated
	 */
	EList<String> getRoles();

} // PageAclEntry
