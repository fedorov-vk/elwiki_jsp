/**
 */
package org.elwiki_data.impl;

import java.lang.reflect.InvocationTargetException;

import java.security.Permission;
import java.security.Principal;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.elwiki_data.Acl;
import org.elwiki_data.AclEntry;
import org.elwiki_data.Elwiki_dataPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Acl</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.impl.AclImpl#getAclEntries <em>Acl Entries</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AclImpl extends CDOObjectImpl implements Acl {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AclImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Elwiki_dataPackage.Literals.ACL;
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
	public EList<AclEntry> getAclEntries() {
		return (EList<AclEntry>)eGet(Elwiki_dataPackage.Literals.ACL__ACL_ENTRIES, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AclEntry getEntry(final Principal principal) {
		for (AclEntry entry : getAclEntries()) {
			String entryPrincipalName = entry.getPrincipal().getName();
			if (entryPrincipalName.equals(principal.getName())) {
				return entry;
			}
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Principal[] findPrincipals(final Permission permission) {
		Vector<Principal> principals = new Vector<Principal>();
		for (AclEntry entry : getAclEntries()) {
			for (Permission perm : entry.getPermission()) {
				if (perm.implies(permission)) {
					principals.add(entry.getPrincipal());
				}
			}
		}
		return principals.toArray(new Principal[principals.size()]);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case Elwiki_dataPackage.ACL___GET_ENTRY__PRINCIPAL:
				return getEntry((Principal)arguments.get(0));
			case Elwiki_dataPackage.ACL___FIND_PRINCIPALS__PERMISSION:
				return findPrincipals((Permission)arguments.get(0));
		}
		return super.eInvoke(operationID, arguments);
	}

} //AclImpl
