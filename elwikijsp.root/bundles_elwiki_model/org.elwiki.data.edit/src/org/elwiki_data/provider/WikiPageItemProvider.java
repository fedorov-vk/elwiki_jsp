/**
 */
package org.elwiki_data.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.WikiPage;

/**
 * This is the item provider adapter for a {@link org.elwiki_data.WikiPage} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class WikiPageItemProvider extends ComparableItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WikiPageItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addIdPropertyDescriptor(object);
			addNamePropertyDescriptor(object);
			addDescriptionPropertyDescriptor(object);
			addAliasPropertyDescriptor(object);
			addRedirectPropertyDescriptor(object);
			addViewCountPropertyDescriptor(object);
			addAttachmentsPropertyDescriptor(object);
			addWikiPropertyDescriptor(object);
			addOldParentsPropertyDescriptor(object);
			addPageReferencesPropertyDescriptor(object);
			addTotalAttachmentPropertyDescriptor(object);
			addWebLogPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Id feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addIdPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_WikiPage_id_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_WikiPage_id_feature", "_UI_WikiPage_type"),
				 Elwiki_dataPackage.Literals.WIKI_PAGE__ID,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Name feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addNamePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_WikiPage_name_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_WikiPage_name_feature", "_UI_WikiPage_type"),
				 Elwiki_dataPackage.Literals.WIKI_PAGE__NAME,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Description feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addDescriptionPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_WikiPage_description_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_WikiPage_description_feature", "_UI_WikiPage_type"),
				 Elwiki_dataPackage.Literals.WIKI_PAGE__DESCRIPTION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Alias feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addAliasPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_WikiPage_alias_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_WikiPage_alias_feature", "_UI_WikiPage_type"),
				 Elwiki_dataPackage.Literals.WIKI_PAGE__ALIAS,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Redirect feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addRedirectPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_WikiPage_redirect_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_WikiPage_redirect_feature", "_UI_WikiPage_type"),
				 Elwiki_dataPackage.Literals.WIKI_PAGE__REDIRECT,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the View Count feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addViewCountPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_WikiPage_viewCount_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_WikiPage_viewCount_feature", "_UI_WikiPage_type"),
				 Elwiki_dataPackage.Literals.WIKI_PAGE__VIEW_COUNT,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Attachments feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addAttachmentsPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_WikiPage_attachments_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_WikiPage_attachments_feature", "_UI_WikiPage_type"),
				 Elwiki_dataPackage.Literals.WIKI_PAGE__ATTACHMENTS,
				 true,
				 false,
				 true,
				 null,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Wiki feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addWikiPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_WikiPage_wiki_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_WikiPage_wiki_feature", "_UI_WikiPage_type"),
				 Elwiki_dataPackage.Literals.WIKI_PAGE__WIKI,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Old Parents feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addOldParentsPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_WikiPage_oldParents_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_WikiPage_oldParents_feature", "_UI_WikiPage_type"),
				 Elwiki_dataPackage.Literals.WIKI_PAGE__OLD_PARENTS,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Page References feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addPageReferencesPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_WikiPage_pageReferences_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_WikiPage_pageReferences_feature", "_UI_WikiPage_type"),
				 Elwiki_dataPackage.Literals.WIKI_PAGE__PAGE_REFERENCES,
				 true,
				 false,
				 true,
				 null,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Total Attachment feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addTotalAttachmentPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_WikiPage_totalAttachment_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_WikiPage_totalAttachment_feature", "_UI_WikiPage_type"),
				 Elwiki_dataPackage.Literals.WIKI_PAGE__TOTAL_ATTACHMENT,
				 false,
				 false,
				 false,
				 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Web Log feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addWebLogPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_WikiPage_webLog_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_WikiPage_webLog_feature", "_UI_WikiPage_type"),
				 Elwiki_dataPackage.Literals.WIKI_PAGE__WEB_LOG,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(Elwiki_dataPackage.Literals.WIKI_PAGE__PAGECONTENTS);
			childrenFeatures.add(Elwiki_dataPackage.Literals.WIKI_PAGE__CHILDREN);
			childrenFeatures.add(Elwiki_dataPackage.Literals.WIKI_PAGE__PAGE_REFERENCES);
			childrenFeatures.add(Elwiki_dataPackage.Literals.WIKI_PAGE__ACL);
			childrenFeatures.add(Elwiki_dataPackage.Literals.WIKI_PAGE__ATTRIBUTES);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns WikiPage.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/WikiPage"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((WikiPage)object).getName();
		return label == null || label.length() == 0 ?
			getString("_UI_WikiPage_type") :
			getString("_UI_WikiPage_type") + " " + label;
	}


	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(WikiPage.class)) {
			case Elwiki_dataPackage.WIKI_PAGE__ID:
			case Elwiki_dataPackage.WIKI_PAGE__NAME:
			case Elwiki_dataPackage.WIKI_PAGE__DESCRIPTION:
			case Elwiki_dataPackage.WIKI_PAGE__ALIAS:
			case Elwiki_dataPackage.WIKI_PAGE__REDIRECT:
			case Elwiki_dataPackage.WIKI_PAGE__VIEW_COUNT:
			case Elwiki_dataPackage.WIKI_PAGE__WIKI:
			case Elwiki_dataPackage.WIKI_PAGE__OLD_PARENTS:
			case Elwiki_dataPackage.WIKI_PAGE__TOTAL_ATTACHMENT:
			case Elwiki_dataPackage.WIKI_PAGE__WEB_LOG:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
			case Elwiki_dataPackage.WIKI_PAGE__PAGECONTENTS:
			case Elwiki_dataPackage.WIKI_PAGE__CHILDREN:
			case Elwiki_dataPackage.WIKI_PAGE__PAGE_REFERENCES:
			case Elwiki_dataPackage.WIKI_PAGE__ACL:
			case Elwiki_dataPackage.WIKI_PAGE__ATTRIBUTES:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
				(Elwiki_dataPackage.Literals.WIKI_PAGE__PAGECONTENTS,
				 Elwiki_dataFactory.eINSTANCE.createPageContent()));

		newChildDescriptors.add
			(createChildParameter
				(Elwiki_dataPackage.Literals.WIKI_PAGE__CHILDREN,
				 Elwiki_dataFactory.eINSTANCE.createWikiPage()));

		newChildDescriptors.add
			(createChildParameter
				(Elwiki_dataPackage.Literals.WIKI_PAGE__PAGE_REFERENCES,
				 Elwiki_dataFactory.eINSTANCE.createPageReference()));

		newChildDescriptors.add
			(createChildParameter
				(Elwiki_dataPackage.Literals.WIKI_PAGE__ACL,
				 Elwiki_dataFactory.eINSTANCE.createAcl()));

		newChildDescriptors.add
			(createChildParameter
				(Elwiki_dataPackage.Literals.WIKI_PAGE__ATTRIBUTES,
				 Elwiki_dataFactory.eINSTANCE.create(Elwiki_dataPackage.Literals.STRING_TO_OBJECT_MAP)));
	}

}
