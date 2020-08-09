/**
 */
package org.elwiki_data.impl;

import java.lang.reflect.InvocationTargetException;
import java.security.Permission;
import java.security.Principal;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.elwiki_data.AclEntry;
import org.elwiki_data.Elwiki_dataPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Acl Entry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.impl.AclEntryImpl#getPrincipal <em>Principal</em>}</li>
 *   <li>{@link org.elwiki_data.impl.AclEntryImpl#getPermission <em>Permission</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AclEntryImpl extends CDOObjectImpl implements AclEntry {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AclEntryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Elwiki_dataPackage.Literals.ACL_ENTRY;
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
	@SuppressWarnings("unchecked")
	@Override
	public EList<Permission> getPermission() {
		return (EList<Permission>)eGet(Elwiki_dataPackage.Literals.ACL_ENTRY__PERMISSION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Boolean checkPermission(final Permission permission) {
		return findPermission(permission) != null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Principal getPrincipal() {
		return (Principal)eGet(Elwiki_dataPackage.Literals.ACL_ENTRY__PRINCIPAL, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPrincipal(Principal newPrincipal) {
		eSet(Elwiki_dataPackage.Literals.ACL_ENTRY__PRINCIPAL, newPrincipal);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case Elwiki_dataPackage.ACL_ENTRY___CHECK_PERMISSION__PERMISSION:
				return checkPermission((Permission)arguments.get(0));
		}
		return super.eInvoke(operationID, arguments);
	}

	/**
	 * Looks through the permission list and finds a permission that matches the permission.
	 * @generated NOT
	 */
	private Permission findPermission(Permission p) {
		for (Permission pp : getPermission()) {
			if (pp.implies(p)) {
				return pp;
			}
		}

		return null;
	}

} //AclEntryImpl
