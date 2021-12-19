package org.elwiki.services;

import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.diff.DifferenceManager;
import org.apache.wiki.api.i18n.InternationalizationManager;
import org.apache.wiki.api.plugin.PluginManager;
import org.apache.wiki.api.references.ReferenceManager;
import org.apache.wiki.api.rss.RSSGenerator;
import org.apache.wiki.api.search.SearchManager;
import org.apache.wiki.api.ui.CommandResolver;
import org.apache.wiki.api.ui.EditorManager;
import org.apache.wiki.api.ui.progress.ProgressManager;
import org.apache.wiki.api.variables.VariableManager;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.IIAuthenticationManager;
import org.apache.wiki.auth.UserManager;
import org.apache.wiki.auth.acl.AclManager;
import org.apache.wiki.content0.PageRenamer;
import org.apache.wiki.filters0.FilterManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.ui.TemplateManager;
import org.apache.wiki.ui.admin0.AdminBeanManager;
import org.apache.wiki.url0.URLConstructor;
import org.apache.wiki.workflow0.WorkflowManager;
import org.elwiki.api.authorization.IAuthorizer;

public class ServicesRefs {

	private static AclManager aclManager;
	private static AttachmentManager attachmentManager;
	private static PageManager pageManager;
	private static PageRenamer pageRenamer;
	private static AuthorizationManager authorizationManager;
	private static IAuthorizer groupManager;
	private static ProgressManager progressManager;
	private static UserManager userManager;
	private static AdminBeanManager adminBeanManager;
	private static IIAuthenticationManager authenticationManager;
	private static RenderingManager renderingManager;
	private static ReferenceManager referenceManager;
	private static VariableManager variableManager;
	private static DifferenceManager differenceManager;
	private static TemplateManager templateManager;
	private static EditorManager editorManager;
	private static PluginManager pluginManager;
	private static FilterManager filterManager;
	private static SearchManager searchManager;
	private static CommandResolver commandResolver;
	private static InternationalizationManager internationalizationManager;
	private static WorkflowManager workflowManager;
	private static URLConstructor urlConstructor;
	private static RSSGenerator rssGenerator;

	// -- service handling --------------------------< start --

	public void setAclManager(AclManager aclManager) {
		ServicesRefs.aclManager = aclManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		ServicesRefs.attachmentManager = attachmentManager;
	}

	public void setPageManager(PageManager pageManager) {
		ServicesRefs.pageManager = pageManager;
	}

	public void setPageRenamer(PageRenamer pageRenamer) {
		ServicesRefs.pageRenamer = pageRenamer;
	}
	
	public void setAuthorizationManager(AuthorizationManager authorizationManager) {
		ServicesRefs.authorizationManager = authorizationManager;
	}

	public void setGroupManager(IAuthorizer groupManager) {
		ServicesRefs.groupManager = groupManager;
	}

	public void setProgressManager(ProgressManager progressManager) {
		ServicesRefs.progressManager = progressManager;
	}

	public void setUserManager(UserManager userManager) {
		ServicesRefs.userManager = userManager;
	}

	public void setAdminBeanManager(AdminBeanManager adminBeanManager) {
		ServicesRefs.adminBeanManager = adminBeanManager;
	}

	public void setAuthenticationManager(IIAuthenticationManager authenticationManager) {
		ServicesRefs.authenticationManager = authenticationManager;
	}

	public void setRenderingManager(RenderingManager renderingManager) {
		ServicesRefs.renderingManager = renderingManager;
	}

	public void setReferenceManager(ReferenceManager referenceManager) {
		ServicesRefs.referenceManager = referenceManager;
	}

	public void setVariableManager(VariableManager variableManager) {
		ServicesRefs.variableManager = variableManager;
	}

	public void setDifferenceManager(DifferenceManager differenceManager) {
		ServicesRefs.differenceManager = differenceManager;
	}

	public void setTemplateManager(TemplateManager templateManager) {
		ServicesRefs.templateManager = templateManager;
	}

	public void setEditorManager(EditorManager editorManager) {
		ServicesRefs.editorManager = editorManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		ServicesRefs.pluginManager = pluginManager;
	}

	public void setFilterManager(FilterManager filterManager) {
		ServicesRefs.filterManager = filterManager;
	}

	public void setSearchManager(SearchManager searchManager) {
		ServicesRefs.searchManager = searchManager;
	}

	public void setCommandResolver(CommandResolver commandResolver) {
		ServicesRefs.commandResolver = commandResolver;
	}

	public void setInternationalizationManager(InternationalizationManager internationalizationManager) {
		ServicesRefs.internationalizationManager = internationalizationManager;
	}

	public void setWorkflowManager(WorkflowManager workflowManager) {
		ServicesRefs.workflowManager = workflowManager;
	}

	public void setUrlConstructor(URLConstructor urlConstructor) {
		ServicesRefs.urlConstructor = urlConstructor;
	}

	public void setRssGenerator(RSSGenerator rssGenerator) {
		ServicesRefs.rssGenerator = rssGenerator;
	}

	// -- service handling ---------------------------- end >--

	public static AclManager getAclManager() {
		return aclManager;
	}

	public static AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public static PageManager getPageManager() {
		return pageManager;
	}

	public static PageRenamer getPageRenamer() {
		return pageRenamer;
	}

	public static AuthorizationManager getAuthorizationManager() {
		return authorizationManager;
	}

	public static IAuthorizer getGroupManager() {
		return groupManager;
	}

	public static ProgressManager getProgressManager() {
		return progressManager;
	}

	public static UserManager getUserManager() {
		return userManager;
	}

	public static AdminBeanManager getAdminBeanManager() {
		return adminBeanManager;
	}

	public static IIAuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public static RenderingManager getRenderingManager() {
		return renderingManager;
	}

	public static ReferenceManager getReferenceManager() {
		return referenceManager;
	}

	public static VariableManager getVariableManager() {
		return variableManager;
	}

	public static DifferenceManager getDifferenceManager() {
		return differenceManager;
	}

	public static TemplateManager getTemplateManager() {
		return templateManager;
	}

	public static EditorManager getEditorManager() {
		return editorManager;
	}

	public static PluginManager getPluginManager() {
		return pluginManager;
	}

	public static FilterManager getFilterManager() {
		return filterManager;
	}

	public static SearchManager getSearchManager() {
		return searchManager;
	}

	public static CommandResolver getCommandResolver() {
		return commandResolver;
	}

	public static InternationalizationManager getInternationalizationManager() {
		return internationalizationManager;
	}

	public static WorkflowManager getWorkflowManager() {
		return workflowManager;
	}

	public static URLConstructor getUrlConstructor() {
		return urlConstructor;
	}

	public static RSSGenerator getRssGenerator() {
		return rssGenerator;
	}

}
