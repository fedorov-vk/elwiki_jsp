$data='
OSGI-INF/commandResolver.xml,OSGI-INF/urlConstructor.xml,OSGI-INF/pageManager.xml,OSGI-INF/pluginManager.xml,OSGI-INF/differenceManager.xml,OSGI-INF/attachmentManager.xml,OSGI-INF/variableManager.xml,OSGI-INF/authenticationManager.xml,OSGI-INF/authorizationManager.xml,OSGI-INF/userManager.xml,OSGI-INF/groupManager.xml,OSGI-INF/editorManager.xml,OSGI-INF/progressManager.xml,OSGI-INF/defaultAclManager.xml,OSGI-INF/workflowManager.xml,OSGI-INF/tasksManager.xml,OSGI-INF/internationalizationManager.xml,OSGI-INF/templateManager.xml,OSGI-INF/filterManager.xml,OSGI-INF/adminBeanManager.xml,OSGI-INF/pageRenamer.xml,OSGI-INF/renderingManager.xml,OSGI-INF/referenceManager.xml,OSGI-INF/aclsSPI.xml,
';
for ( split(",", $data) ) {
    print " $_,\n";
}
