use strict;

my $path = '/home/vfedorov/dev/dev_wikijsp/GIT/elwiki+jsp_20211113/elwikijsp.root/products/elwiki.product/target/products/elwiki.product/linux/gtk/x86_64/plugins';

my %modes = (
    'org.eclipse.osgi' => '-1:true',
    'org.apache.felix.scr' => '1:true',
    'org.eclipse.core.filesystem.linux.x86_64' => 'default:false',
    'org.eclipse.core.runtime' => 'default:true',
    'org.eclipse.equinox.common' => '2:true',
    'org.eclipse.osgi' => '-1:true'
);

while(<$path/*jar>) {
    substr($_, 0, 1 + length($path)) = '';
#    chomp; s/^s+//s; s/\s+$//s;
    s/_.+?$//;
    my $mode = 'default:default'; # по умолчанию.
    $mode = $modes{$_} if( exists($modes{$_}) );

    my $entry = $_.'@'.$mode;
    print <<EOL;
        <setEntry value="$entry"/>
EOL
    #printf("%-71s %s\n", $_, $mode);
}

__DATA__
        <setEntry value="com.google.gson*2.7.0.v20170129-0911@default:default"/>
        <setEntry value="com.google.gson*2.8.7.v20210624-1215@default:default"/>
        <setEntry value="com.ibm.icu@default:default"/>
        <setEntry value="com.sun.el@default:default"/>
        <setEntry value="jakarta.servlet-api@default:default"/>
        <setEntry value="jakarta.servlet@default:default"/>
        <setEntry value="javax.activation*1.1.0.v201211130549@default:default"/>
        <setEntry value="javax.activation*1.2.2.v20201119-1642@default:default"/>
        <setEntry value="javax.annotation@default:default"/>
        <setEntry value="javax.el@default:default"/>
        <setEntry value="javax.inject@default:default"/>
        <setEntry value="javax.mail@default:default"/>
        <setEntry value="javax.servlet.jsp@default:default"/>
        <setEntry value="javax.xml.bind@default:default"/>
        <setEntry value="javax.xml@default:default"/>
        <setEntry value="lpg.runtime.java@default:default"/>
        <setEntry value="org.apache.commons.codec@default:default"/>
        <setEntry value="org.apache.commons.io@default:default"/>
        <setEntry value="org.apache.felix.gogo.command@default:default"/>
        <setEntry value="org.apache.felix.gogo.runtime@default:default"/>
        <setEntry value="org.apache.felix.gogo.shell@default:default"/>
        <setEntry value="org.apache.felix.scr@1:true"/>
        <setEntry value="org.apache.jasper.glassfish@default:default"/>
        <setEntry value="org.apache.log4j@default:default"/>
        <setEntry value="org.apache.lucene.analyzers-common*6.1.0.v20161115-1612@default:default"/>
        <setEntry value="org.apache.lucene.analyzers-common*7.5.0.v20181003-1532@default:default"/>
        <setEntry value="org.apache.lucene.analyzers-common*8.4.1.v20200122-1459@default:default"/>
        <setEntry value="org.apache.lucene.analyzers-common*8.5.1@default:default"/>
        <setEntry value="org.apache.lucene.core*6.1.0.v20170814-1820@default:default"/>
        <setEntry value="org.apache.lucene.core*7.5.0.v20181003-1532@default:default"/>
        <setEntry value="org.apache.lucene.core*8.4.1.v20200122-1459@default:default"/>
        <setEntry value="org.apache.lucene.core*8.5.1@default:default"/>
        <setEntry value="org.apache.xalan@default:default"/>
        <setEntry value="org.apache.xerces*2.12.1.v20210115-0812@default:default"/>
        <setEntry value="org.apache.xerces*2.9.0.v201101211617@default:default"/>
        <setEntry value="org.apache.xml.resolver@default:default"/>
        <setEntry value="org.apache.xml.serializer@default:default"/>
        <setEntry value="org.eclipse.core.commands@default:default"/>
        <setEntry value="org.eclipse.core.contenttype@default:default"/>
        <setEntry value="org.eclipse.core.databinding.observable@default:default"/>
        <setEntry value="org.eclipse.core.databinding.property@default:default"/>
        <setEntry value="org.eclipse.core.databinding@default:default"/>
        <setEntry value="org.eclipse.core.expressions@default:default"/>
        <setEntry value="org.eclipse.core.filesystem.linux.x86_64@default:false"/>
        <setEntry value="org.eclipse.core.filesystem@default:default"/>
        <setEntry value="org.eclipse.core.jobs@default:default"/>
        <setEntry value="org.eclipse.core.resources@default:default"/>
        <setEntry value="org.eclipse.core.runtime@default:true"/>
        <setEntry value="org.eclipse.emf.common@default:default"/>
        <setEntry value="org.eclipse.emf.databinding@default:default"/>
        <setEntry value="org.eclipse.emf.ecore.change@default:default"/>
        <setEntry value="org.eclipse.emf.ecore.edit@default:default"/>
        <setEntry value="org.eclipse.emf.ecore.xmi@default:default"/>
        <setEntry value="org.eclipse.emf.ecore@default:default"/>
        <setEntry value="org.eclipse.emf.edit@default:default"/>
        <setEntry value="org.eclipse.equinox.app@default:default"/>
        <setEntry value="org.eclipse.equinox.common@2:true"/>
        <setEntry value="org.eclipse.equinox.console@default:default"/>
        <setEntry value="org.eclipse.equinox.event@default:default"/>
        <setEntry value="org.eclipse.equinox.http.jetty@default:default"/>
        <setEntry value="org.eclipse.equinox.http.registry@default:default"/>
        <setEntry value="org.eclipse.equinox.http.servlet@default:default"/>
        <setEntry value="org.eclipse.equinox.jsp.jasper.registry@default:default"/>
        <setEntry value="org.eclipse.equinox.jsp.jasper@default:default"/>
        <setEntry value="org.eclipse.equinox.preferences@default:default"/>
        <setEntry value="org.eclipse.equinox.registry@default:default"/>
        <setEntry value="org.eclipse.equinox.security@default:default"/>
        <setEntry value="org.eclipse.jdt.annotation*2.2.600.v20200408-1511@default:default"/>
        <setEntry value="org.eclipse.jetty.http*10.0.6@default:default"/>
        <setEntry value="org.eclipse.jetty.http*9.4.24.v20191120@default:default"/>
        <setEntry value="org.eclipse.jetty.io*10.0.6@default:default"/>
        <setEntry value="org.eclipse.jetty.io*9.4.24.v20191120@default:default"/>
        <setEntry value="org.eclipse.jetty.security*10.0.6@default:default"/>
        <setEntry value="org.eclipse.jetty.security*9.4.24.v20191120@default:default"/>
        <setEntry value="org.eclipse.jetty.server*10.0.6@default:default"/>
        <setEntry value="org.eclipse.jetty.server*9.4.24.v20191120@default:default"/>
        <setEntry value="org.eclipse.jetty.servlet*10.0.6@default:default"/>
        <setEntry value="org.eclipse.jetty.servlet*9.4.24.v20191120@default:default"/>
        <setEntry value="org.eclipse.jetty.util*10.0.6@default:default"/>
        <setEntry value="org.eclipse.jetty.util*9.4.24.v20191120@default:default"/>
        <setEntry value="org.eclipse.jetty.webapp@default:default"/>
        <setEntry value="org.eclipse.jetty.xml@default:default"/>
        <setEntry value="org.eclipse.jface@default:default"/>
        <setEntry value="org.eclipse.ocl.common@default:default"/>
        <setEntry value="org.eclipse.ocl.ecore@default:default"/>
        <setEntry value="org.eclipse.ocl@default:default"/>
        <setEntry value="org.eclipse.osgi.services@default:default"/>
        <setEntry value="org.eclipse.osgi.util@default:default"/>
        <setEntry value="org.eclipse.osgi@-1:true"/>
        <setEntry value="org.eclipse.swt@default:default"/>
        <setEntry value="org.slf4j.api@default:default"/>
