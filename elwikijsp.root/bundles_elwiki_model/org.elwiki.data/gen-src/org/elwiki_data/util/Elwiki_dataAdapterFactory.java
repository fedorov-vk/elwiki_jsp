/**
 */
package org.elwiki_data.util;

import java.security.Principal;

import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.elwiki_data.Acl;
import org.elwiki_data.AclEntry;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.IHistoryInfo;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.PagesStore;
import org.elwiki_data.WikiPage;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.elwiki_data.Elwiki_dataPackage
 * @generated
 */
public class Elwiki_dataAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static Elwiki_dataPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Elwiki_dataAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = Elwiki_dataPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Elwiki_dataSwitch<Adapter> modelSwitch =
		new Elwiki_dataSwitch<Adapter>() {
			@Override
			public Adapter caseWikiPage(WikiPage object) {
				return createWikiPageAdapter();
			}
			@Override
			public Adapter casePagesStore(PagesStore object) {
				return createPagesStoreAdapter();
			}
			@Override
			public Adapter casePageContent(PageContent object) {
				return createPageContentAdapter();
			}
			@Override
			public Adapter casePageAttachment(PageAttachment object) {
				return createPageAttachmentAdapter();
			}
			@Override
			public Adapter caseObject(Object object) {
				return createObjectAdapter();
			}
			@Override
			public Adapter caseComparable(Comparable object) {
				return createComparableAdapter();
			}
			@Override
			public Adapter caseCloneable(Cloneable object) {
				return createCloneableAdapter();
			}
			@Override
			public Adapter caseIHistoryInfo(IHistoryInfo object) {
				return createIHistoryInfoAdapter();
			}
			@Override
			public Adapter casePageReference(PageReference object) {
				return createPageReferenceAdapter();
			}
			@Override
			public Adapter caseAclEntry(AclEntry object) {
				return createAclEntryAdapter();
			}
			@Override
			public Adapter casePrincipal(Principal object) {
				return createPrincipalAdapter();
			}
			@Override
			public Adapter caseAcl(Acl object) {
				return createAclAdapter();
			}
			@Override
			public Adapter caseStringToObjectMap(Map.Entry<String, Object> object) {
				return createStringToObjectMapAdapter();
			}
			@Override
			public Adapter caseAttachmentContent(AttachmentContent object) {
				return createAttachmentContentAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link org.elwiki_data.WikiPage <em>Wiki Page</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.elwiki_data.WikiPage
	 * @generated
	 */
	public Adapter createWikiPageAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.elwiki_data.PagesStore <em>Pages Store</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.elwiki_data.PagesStore
	 * @generated
	 */
	public Adapter createPagesStoreAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.elwiki_data.PageContent <em>Page Content</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.elwiki_data.PageContent
	 * @generated
	 */
	public Adapter createPageContentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.elwiki_data.PageAttachment <em>Page Attachment</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.elwiki_data.PageAttachment
	 * @generated
	 */
	public Adapter createPageAttachmentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.lang.Object <em>Object</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.lang.Object
	 * @generated
	 */
	public Adapter createObjectAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.lang.Comparable <em>Comparable</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.lang.Comparable
	 * @generated
	 */
	public Adapter createComparableAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.lang.Cloneable <em>Cloneable</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.lang.Cloneable
	 * @generated
	 */
	public Adapter createCloneableAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.elwiki_data.IHistoryInfo <em>IHistory Info</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.elwiki_data.IHistoryInfo
	 * @generated
	 */
	public Adapter createIHistoryInfoAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.elwiki_data.PageReference <em>Page Reference</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.elwiki_data.PageReference
	 * @generated
	 */
	public Adapter createPageReferenceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.elwiki_data.AclEntry <em>Acl Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.elwiki_data.AclEntry
	 * @generated
	 */
	public Adapter createAclEntryAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.security.Principal <em>Principal</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.security.Principal
	 * @generated
	 */
	public Adapter createPrincipalAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.elwiki_data.Acl <em>Acl</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.elwiki_data.Acl
	 * @generated
	 */
	public Adapter createAclAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>String To Object Map</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createStringToObjectMapAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.elwiki_data.AttachmentContent <em>Attachment Content</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.elwiki_data.AttachmentContent
	 * @generated
	 */
	public Adapter createAttachmentContentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //Elwiki_dataAdapterFactory
