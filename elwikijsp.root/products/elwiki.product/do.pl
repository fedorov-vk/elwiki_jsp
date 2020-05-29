while(<DATA>) {
    chomp; next if($_ eq '');
    s/^\d+\s+ACTIVE\s+//;
    s/_.+$//;
#print "$_\n";
print <<EOL;
	'reference\\:file\\:$_/\@4\\:start',
EOL
}

__DATA__
2	ACTIVE      javax.el_2.2.0.v201303151357
3	ACTIVE      org.apache.commons.fileupload_1.3.2.v20170320-2229
4	ACTIVE      org.apache.commons.io_2.6.0.v20190123-2029
5	ACTIVE      org.apache.commons.logging_1.2.0.v20180409-1502
6	ACTIVE      org.apache.felix.gogo.command_1.0.2.v20170914-1324
7	ACTIVE      org.apache.felix.gogo.runtime_1.1.0.v20180713-1646
8	ACTIVE      org.apache.felix.gogo.shell_1.1.0.v20180713-1646
9	ACTIVE      org.apache.felix.webconsole_4.3.16
10	ACTIVE      org.apache.jasper.glassfish_2.2.2.v201501141630
11	ACTIVE      org.apache.taglibs.standard-compat_1.2.1
12	ACTIVE      org.apache.taglibs.standard-impl_1.2.1
13	ACTIVE      org.apache.taglibs.standard-jstlel_1.2.1
14	ACTIVE      org.apache.taglibs.taglibs-standard-spec_1.2.1
15	ACTIVE      org.apache.xalan_2.7.1.v201005080400
16	ACTIVE      org.apache.xml.serializer_2.7.1.v201005080400
17	ACTIVE      org.eclipse.equinox.common_3.11.0.v20200206-0817
18	ACTIVE      org.eclipse.equinox.console_1.4.0.v20190819-1430
19	ACTIVE      org.eclipse.equinox.http.helper_1.0.0.202005181331
20	ACTIVE      org.eclipse.equinox.http.registry_1.1.700.v20190214-1948
21	ACTIVE      org.eclipse.equinox.http.servlet_1.6.400.v20191213-1757
22	ACTIVE      org.eclipse.equinox.http.servletbridge_1.1.100.v20180827-1235
23	ACTIVE      org.eclipse.equinox.jsp.jasper_1.1.400.v20191213-1757
24	ACTIVE      org.eclipse.equinox.jsp.jasper.registry_1.1.300.v20190714-1850
25	ACTIVE      org.eclipse.equinox.registry_3.8.700.v20200121-1457
26	ACTIVE      org.eclipse.equinox.servletbridge_1.5.300.v20200113-1046
27	ACTIVE      org.eclipse.osgi.services_3.8.0.v20190206-2147
28	ACTIVE      org.eclipse.osgi.util_3.5.300.v20190708-1141
29	ACTIVE      sample.http_1.0.0
30	ACTIVE      sample.res_1.0.0.202005181331
