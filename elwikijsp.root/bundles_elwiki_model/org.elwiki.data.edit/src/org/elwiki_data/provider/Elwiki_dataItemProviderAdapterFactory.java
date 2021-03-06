/**
 */
package org.elwiki_data.provider;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

import org.elwiki_data.util.Elwiki_dataAdapterFactory;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class Elwiki_dataItemProviderAdapterFactory extends Elwiki_dataAdapterFactory implements ComposeableAdapterFactory, IChangeNotifier, IDisposable {
	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;

	/**
	 * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

	/**
	 * This constructs an instance.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Elwiki_dataItemProviderAdapterFactory() {
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
		supportedTypes.add(IItemPropertySource.class);
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.elwiki_data.WikiPage} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WikiPageItemProvider wikiPageItemProvider;

	/**
	 * This creates an adapter for a {@link org.elwiki_data.WikiPage}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createWikiPageAdapter() {
		if (wikiPageItemProvider == null) {
			wikiPageItemProvider = new WikiPageItemProvider(this);
		}

		return wikiPageItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.elwiki_data.PagesStore} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PagesStoreItemProvider pagesStoreItemProvider;

	/**
	 * This creates an adapter for a {@link org.elwiki_data.PagesStore}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createPagesStoreAdapter() {
		if (pagesStoreItemProvider == null) {
			pagesStoreItemProvider = new PagesStoreItemProvider(this);
		}

		return pagesStoreItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.elwiki_data.PageContent} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PageContentItemProvider pageContentItemProvider;

	/**
	 * This creates an adapter for a {@link org.elwiki_data.PageContent}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createPageContentAdapter() {
		if (pageContentItemProvider == null) {
			pageContentItemProvider = new PageContentItemProvider(this);
		}

		return pageContentItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.elwiki_data.PageAttachment} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PageAttachmentItemProvider pageAttachmentItemProvider;

	/**
	 * This creates an adapter for a {@link org.elwiki_data.PageAttachment}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createPageAttachmentAdapter() {
		if (pageAttachmentItemProvider == null) {
			pageAttachmentItemProvider = new PageAttachmentItemProvider(this);
		}

		return pageAttachmentItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link java.lang.Comparable} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComparableItemProvider comparableItemProvider;

	/**
	 * This creates an adapter for a {@link java.lang.Comparable}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createComparableAdapter() {
		if (comparableItemProvider == null) {
			comparableItemProvider = new ComparableItemProvider(this);
		}

		return comparableItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link java.lang.Cloneable} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CloneableItemProvider cloneableItemProvider;

	/**
	 * This creates an adapter for a {@link java.lang.Cloneable}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createCloneableAdapter() {
		if (cloneableItemProvider == null) {
			cloneableItemProvider = new CloneableItemProvider(this);
		}

		return cloneableItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.elwiki_data.PageReference} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PageReferenceItemProvider pageReferenceItemProvider;

	/**
	 * This creates an adapter for a {@link org.elwiki_data.PageReference}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createPageReferenceAdapter() {
		if (pageReferenceItemProvider == null) {
			pageReferenceItemProvider = new PageReferenceItemProvider(this);
		}

		return pageReferenceItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.elwiki_data.AclEntry} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AclEntryItemProvider aclEntryItemProvider;

	/**
	 * This creates an adapter for a {@link org.elwiki_data.AclEntry}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createAclEntryAdapter() {
		if (aclEntryItemProvider == null) {
			aclEntryItemProvider = new AclEntryItemProvider(this);
		}

		return aclEntryItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.elwiki_data.Acl} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AclItemProvider aclItemProvider;

	/**
	 * This creates an adapter for a {@link org.elwiki_data.Acl}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createAclAdapter() {
		if (aclItemProvider == null) {
			aclItemProvider = new AclItemProvider(this);
		}

		return aclItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link java.util.Map.Entry} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected StringToObjectMapItemProvider stringToObjectMapItemProvider;

	/**
	 * This creates an adapter for a {@link java.util.Map.Entry}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createStringToObjectMapAdapter() {
		if (stringToObjectMapItemProvider == null) {
			stringToObjectMapItemProvider = new StringToObjectMapItemProvider(this);
		}

		return stringToObjectMapItemProvider;
	}

	/**
	 * This returns the root adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ComposeableAdapterFactory getRootAdapterFactory() {
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * This sets the composed adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
		this.parentAdapterFactory = parentAdapterFactory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object type) {
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter adapt(Notifier notifier, Object type) {
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object adapt(Object object, Object type) {
		if (isFactoryForType(type)) {
			Object adapter = super.adapt(object, type);
			if (!(type instanceof Class<?>) || (((Class<?>)type).isInstance(adapter))) {
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This removes a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void fireNotifyChanged(Notification notification) {
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null) {
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

	/**
	 * This disposes all of the item providers created by this factory. 
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void dispose() {
		if (wikiPageItemProvider != null) wikiPageItemProvider.dispose();
		if (pagesStoreItemProvider != null) pagesStoreItemProvider.dispose();
		if (pageContentItemProvider != null) pageContentItemProvider.dispose();
		if (pageAttachmentItemProvider != null) pageAttachmentItemProvider.dispose();
		if (comparableItemProvider != null) comparableItemProvider.dispose();
		if (cloneableItemProvider != null) cloneableItemProvider.dispose();
		if (pageReferenceItemProvider != null) pageReferenceItemProvider.dispose();
		if (aclEntryItemProvider != null) aclEntryItemProvider.dispose();
		if (aclItemProvider != null) aclItemProvider.dispose();
		if (stringToObjectMapItemProvider != null) stringToObjectMapItemProvider.dispose();
	}

}
