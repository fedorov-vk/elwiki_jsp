/**
 */
package org.elwiki_data;

import java.security.Permission;
import java.security.Principal;
import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Acl</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.Acl#getAclEntries <em>Acl Entries</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getAcl()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Acl extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Acl Entries</b></em>' containment reference list.
	 * The list contents are of type {@link org.elwiki_data.AclEntry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Acl Entries</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Acl Entries</em>' containment reference list.
	 * @see org.elwiki_data.Elwiki_dataPackage#getAcl_AclEntries()
	 * @model containment="true"
	 * @generated
	 */
	EList<AclEntry> getAclEntries();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model principalDataType="org.elwiki_data.PrincipalObject"
	 * @generated
	 */
	AclEntry getEntry(Principal principal);

	Principal[] findPrincipals(Permission view);

} // Acl
