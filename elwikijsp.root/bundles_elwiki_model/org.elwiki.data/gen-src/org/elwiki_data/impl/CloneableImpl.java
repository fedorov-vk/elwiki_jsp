/**
 */
package org.elwiki_data.impl;

import java.lang.Cloneable;
import java.lang.Object;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.elwiki_data.Elwiki_dataPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Cloneable</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class CloneableImpl extends CDOObjectImpl implements Cloneable {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CloneableImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Elwiki_dataPackage.Literals.CLONEABLE;
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
	public Object clone() {
		Assert.isTrue(false, ":FVK: Code of clone must be implemented.");
		Object o = new Object();
		return o;
		
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case Elwiki_dataPackage.CLONEABLE___CLONE:
				return clone();
		}
		return super.eInvoke(operationID, arguments);
	}

} //CloneableImpl
