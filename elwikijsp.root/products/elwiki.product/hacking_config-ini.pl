#!/usr/bin/perl
# "хак.pl v1.0" -- для "config.ini"
#
# 1. Распаковка из ../content.jar
# 2. Модификация - в "content.xml"
# 3. Упаковка - в "../content.jar"
#
use Cwd;
my $dir = getcwd;

my $PLACE = 'products/elwiki.product/linux/gtk/x86_64/configuration';
my $FILE = 'config.ini';
my @content = ();

my @bundles4run = (
	'reference\:file\:javax.servlet/@2\:start',
	'reference\:file\:javax.servlet.jsp/@2\:start',
	'reference\:file\:javax.xml/@2\:start',
	'reference\:file\:javax.el/@2\:start',
	'reference\:file\:org.apache.commons.fileupload/@2\:start',
	'reference\:file\:org.apache.commons.io/@2\:start',
	'reference\:file\:org.apache.commons.logging/@2\:start',
	'reference\:file\:org.apache.felix.gogo.command/@2\:start',
	'reference\:file\:org.apache.felix.gogo.runtime/@2\:start',
	'reference\:file\:org.apache.felix.gogo.shell/@2\:start',
	'reference\:file\:org.apache.felix.webconsole/@2\:start',
	'reference\:file\:org.apache.jasper.glassfish/@3',
	'reference\:file\:org.apache.taglibs.standard.glassfish/@3',
	'reference\:file\:org.eclipse.equinox.common/@2\:start',
	'reference\:file\:org.eclipse.equinox.console/@2\:start',
	'reference\:file\:org.eclipse.equinox.http.registry/@2\:start',
	'reference\:file\:org.eclipse.equinox.http.servlet/@2\:start',
	'reference\:file\:org.eclipse.equinox.http.servletbridge/@2\:start',
	'reference\:file\:org.eclipse.equinox.jsp.jasper/@2\:start',
	'reference\:file\:org.eclipse.equinox.jsp.jasper.registry/@2\:start',
	'reference\:file\:org.eclipse.equinox.registry/@1\:start',
	'reference\:file\:org.eclipse.osgi.services/@1\:start',
	'reference\:file\:org.eclipse.osgi.util/@1\:start',
	'reference\:file\:sample.http/@4\:start',
	'reference\:file\:sample.res/@4\:start',
);
#	'reference\:file\:org.eclipse.equinox.servletbridge/@4\:start',
#	'reference\:file\:org.apache.taglibs.standard-compat/@4\:start',
#	'reference\:file\:org.apache.taglibs.standard-impl/@4\:start',
#	'reference\:file\:org.apache.taglibs.standard-jstlel/@4\:start',
#	'reference\:file\:org.apache.taglibs.taglibs-standard-spec/@4\:start',
# 	'reference\:file\:org.eclipse.update.configurator/@4\:start',


sub addBundles {
#	push @bundles, @bundles4run;
	my $rBundles = shift;
L1:
	for my $bundle (@bundles4run) {
		$bundle =~ m/reference\\:file\\:([^_\/]+)/;
		my $name = $1;
		for (@$rBundles) {
			if( /$name/ ) { next L1; }
		}
		push @$rBundles, $bundle;
	}
}

open(IN, "<$PLACE/$FILE") or die "Fail open '$FILE'\n$!";
while(<IN>) {
	unless(/^\s*osgi\.bundles=(.+)$/) { push @content,$_; next; }
	my @bundles = split(",", $1);
	addBundles(\@bundles);
	push @content, 'osgi\.bundles='.(shift @bundles).",\\\n";  # первая строка.
	foreach(@bundles) { $_='  '.$_; }                          # добавить ведущие пробелы
	push @content, join(",\\\n", @bundles)."\n";
}
close(IN);

#open(OUT, ">$FILE") or die "Fail create '$FILE'\n$!\n";
open(OUT, ">$PLACE/$FILE") or die "Fail create '$FILE'\n$!\n";
print OUT @content;
close(OUT);
