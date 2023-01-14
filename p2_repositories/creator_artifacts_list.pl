#!/usr/bin/perl
#
# Creates a list of artifact specifications for creating a p2 repository.
# This list for the file pom.xml in subdirectories.
#
use 5.30.0; use warnings;

while(<DATA>) {
    chomp;
    next if($_ eq '');

#    if( /^(.+?\w)-(\d+?\.\d+?\.\d+?).jar/ ) { print "$1\n$2\n"; }
print <<EOL;
				<artifact><id>$_</id><source>true</source></artifact>
EOL
}

my $additional_artifacts = <<"EOT";
org.jdom:jdom2:2.0.6
jaxen:jaxen:1.2.0
net.sf.ehcache:ehcache:2.10.6					- :FVK: for a cached Attachment manager.
net.sourceforge:akismet-java:1.02
net.sourceforge:sandler:0.5
org.jvnet.hudson:org.suigeneris.jrcs.diff:0.4.2
org.freshcookies:freshcookies-security:0.60		- not required?
EOT
#org.apache.lucene:lucene-highlighter:8.5.1
#org.apache.lucene:lucene-queryparser:8.5.1
#org.apache.lucene:lucene-analyzers-common:8.5.1
#org.apache.lucene.analyzers-common_8.5.1
#org.apache.lucene.core_8.5.1
#org.apache.lucene.highlighter_8.5.1
#org.apache.lucene.memory_8.5.1
#org.apache.lucene.queries_8.5.1
#org.apache.lucene.queryparser_8.5.1
#org.apache.lucene.sandbox_8.5.1

my $apache_tools = << "TEXT";
org.apache.felix:org.apache.felix.webconsole:4.3.16
javax.servlet.jsp.jstl:javax.servlet.jsp.jstl-api:1.2.1
org.apache.taglibs.standard:glassfish:1.2.0
org.apache.taglibs:taglibs-standard-impl:1.2.1
org.apache.taglibs:taglibs-standard-compat:1.2.1
org.apache.taglibs:taglibs-standard-spec:1.2.1
org.apache.taglibs:taglibs-standard-jstlel:1.2.1
TEXT
#org.apache.xalan:xalan:2.7.1
#org.apache.xml:serializer:2.7.1

my $custom_p2_site = <<"EOT";
org.apache.felix:org.apache.felix.webconsole:4.3.16
antlr:antlr:2.7.2
commons-beanutils:commons-beanutils:1.7.0
commons-fileupload:commons-fileupload:1.0
commons-validator:commons-validator:1.1.4
EOT
#commons-collections_2.1.0
#commons-digester_1.6.0
#struts_1.2.9
#xml-apis_1.0.0.b2

my $for_test_site = <<"EOT";
org.mockito:mockito-core:4.2.0
EOT

my $from_eclipse =  <<"EOT";
oro:oro:2.0.8
javax.el:javax.el-api:3.0.0
EOT

my $from_eclipseX = <<"EOT";
apache-oro:jakarta-oro:2.0.8
EOT

__DATA__
xmlrpc:xmlrpc:2.0.1
org.apache.felix:org.apache.felix.webconsole:4.3.16
javax.servlet.jsp.jstl:javax.servlet.jsp.jstl-api:1.2.1
org.apache.taglibs.standard:glassfish:1.2.0
org.apache.taglibs:taglibs-standard-impl:1.2.1
org.apache.taglibs:taglibs-standard-compat:1.2.1
org.apache.taglibs:taglibs-standard-spec:1.2.1
org.apache.taglibs:taglibs-standard-jstlel:1.2.1
org.apache.xalan:xalan:2.7.1
org.apache.xml:serializer:2.7.1
