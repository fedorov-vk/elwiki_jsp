/**
 */
package org.elwiki_data;

import java.security.Permission;
import java.security.Principal;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Acl Entry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.AclEntry#getPrincipal <em>Principal</em>}</li>
 *   <li>{@link org.elwiki_data.AclEntry#getPermission <em>Permission</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getAclEntry()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface AclEntry extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Principal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Principal</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Principal</em>' attribute.
	 * @see #setPrincipal(Principal)
	 * @see org.elwiki_data.Elwiki_dataPackage#getAclEntry_Principal()
	 * @model dataType="org.elwiki_data.PrincipalObject"
	 * @generated
	 */
	Principal getPrincipal();

	/**
	 * Sets the value of the '{@link org.elwiki_data.AclEntry#getPrincipal <em>Principal</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Principal</em>' attribute.
	 * @see #getPrincipal()
	 * @generated
	 */
	void setPrincipal(Principal value);

	/**
	 * Returns the value of the '<em><b>Permission</b></em>' attribute list.
	 * The list contents are of type {@link java.security.Permission}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Permission</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Permission</em>' attribute list.
	 * @see org.elwiki_data.Elwiki_dataPackage#getAclEntry_Permission()
	 * @model dataType="org.elwiki_data.PermissionObject"
	 * @generated
	 */
	EList<Permission> getPermission();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * 
	 * <!-- end-model-doc -->
	 * @model permissionDataType="org.elwiki_data.PermissionObject"
	 * @generated
	 */
	Boolean checkPermission(Permission permission);

} // AclEntry
