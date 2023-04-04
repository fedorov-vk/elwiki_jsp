/**
 */
package org.elwiki_data.impl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.elwiki_data.AclInfo;
import org.elwiki_data.Elwiki_dataPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Acl Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.impl.AclInfoImpl#isAllow <em>Allow</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AclInfoImpl#getPermission <em>Permission</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AclInfoImpl#getRoles <em>Roles</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AclInfoImpl extends CDOObjectImpl implements AclInfo {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AclInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Elwiki_dataPackage.Literals.ACL_INFO;
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
	public boolean isAllow() {
		return (Boolean)eGet(Elwiki_dataPackage.Literals.ACL_INFO__ALLOW, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAllow(boolean newAllow) {
		eSet(Elwiki_dataPackage.Literals.ACL_INFO__ALLOW, newAllow);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPermission() {
		return (String)eGet(Elwiki_dataPackage.Literals.ACL_INFO__PERMISSION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPermission(String newPermission) {
		eSet(Elwiki_dataPackage.Literals.ACL_INFO__PERMISSION, newPermission);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EList<String> getRoles() {
		return (EList<String>)eGet(Elwiki_dataPackage.Literals.ACL_INFO__ROLES, true);
	}

} //AclInfoImpl
