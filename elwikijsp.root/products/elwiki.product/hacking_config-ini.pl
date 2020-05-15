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
	'reference\:file\:javax.servlet/@4\:start',
	'reference\:file\:javax.servlet.jsp/@4\:start',
	'reference\:file\:javax.servlet.jsp.jstl-api/@4\:start',
	'reference\:file\:javax.xml/@4\:start',
	'reference\:file\:org.apache.commons.fileupload/@4\:start',
	'reference\:file\:org.apache.commons.io/@4\:start',
	'reference\:file\:org.apache.commons.logging/@4\:start',
	'reference\:file\:org.apache.felix.gogo.command_1.0.2.v20170914-1324.jar@4\:start',
	'reference\:file\:org.apache.felix.gogo.runtime_1.1.0.v20180713-1646.jar@4\:start',
	'reference\:file\:org.apache.felix.gogo.shell_1.1.0.v20180713-1646.jar@4\:start',
	'reference\:file\:org.apache.felix.webconsole_4.3.16.jar@4\:start',
	'reference\:file\:org.apache.jasper.glassfish_2.2.2.v201501141630.jar@4',
);
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
