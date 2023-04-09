/**
 */
package org.elwiki_data.impl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.PageAclEntry;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Page Acl Entry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.impl.PageAclEntryImpl#isAllow <em>Allow</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAclEntryImpl#getPermission <em>Permission</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageAclEntryImpl#getRoles <em>Roles</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PageAclEntryImpl extends CDOObjectImpl implements PageAclEntry {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PageAclEntryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Elwiki_dataPackage.Literals.PAGE_ACL_ENTRY;
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
		return (Boolean)eGet(Elwiki_dataPackage.Literals.PAGE_ACL_ENTRY__ALLOW, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAllow(boolean newAllow) {
		eSet(Elwiki_dataPackage.Literals.PAGE_ACL_ENTRY__ALLOW, newAllow);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPermission() {
		return (String)eGet(Elwiki_dataPackage.Literals.PAGE_ACL_ENTRY__PERMISSION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPermission(String newPermission) {
		eSet(Elwiki_dataPackage.Literals.PAGE_ACL_ENTRY__PERMISSION, newPermission);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EList<String> getRoles() {
		return (EList<String>)eGet(Elwiki_dataPackage.Literals.PAGE_ACL_ENTRY__ROLES, true);
	}

} //PageAclEntryImpl
