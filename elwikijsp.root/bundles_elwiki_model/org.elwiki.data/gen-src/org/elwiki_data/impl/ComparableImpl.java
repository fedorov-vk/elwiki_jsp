/**
 */
package org.elwiki_data.impl;

import java.lang.Comparable;
import java.lang.Object;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.elwiki_data.Elwiki_dataPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Comparable</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class ComparableImpl extends CDOObjectImpl implements Comparable {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComparableImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Elwiki_dataPackage.Literals.COMPARABLE;
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
	public int compareTo(final Object obj) {
		Assert.isTrue(false, ":FVK: Code of compareTo must be implemented.");
		return 0;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case Elwiki_dataPackage.COMPARABLE___COMPARE_TO__OBJECT:
				return compareTo((Object)arguments.get(0));
		}
		return super.eInvoke(operationID, arguments);
	}

} //ComparableImpl
