#!/usr/bin/perl
#
# Creates a list of artifact specifications for creating a p2 repository.
#  - convert from maven POM syntax to group:id:version syntax.
#
# Into DATA area - put following blocks of lines:
#  <groupId>org.apache.lucene</groupId>
#    <artifactId>lucene-analyzers-common</artifactId>
#  <version>8.5.1</version>
#
# @author V.Fedorov
#
use strict;
#use 5.30.0; use warnings;

my $data;
while(<DATA>) {
    chomp; s/^\s+$//; s/\s+$//s;
    next if($_ eq '');
    $data.=$_;
}

$data =~ s|<groupId>\s*?([^<]+?)\s*?</groupId>\s*?<artifactId>\s*?([^<]+?)\s*?</artifactId>\s*?<version>\s*?([^<]+?)\s*?</version>|
    sprintf("\t\t\t\t<artifact><id>%s:%s:%s</id><source>true</source></artifact>\n", $1, $2, $3);
|eg;

print $data;

__DATA__
<groupId>org.apache.lucene</groupId>
    <artifactId>lucene-core</artifactId>
<version>9.4.2</version>
<groupId>org.apache.lucene</groupId>
    <artifactId>lucene-analysis-common</artifactId>
    <version>9.4.2</version>
<groupId>org.apache.lucene</groupId>
    <artifactId>lucene-queries</artifactId>
    <version>9.4.2</version>
<groupId>org.apache.lucene</groupId>
    <artifactId>lucene-highlighter</artifactId>
    <version>9.4.2</version>
<groupId>org.apache.lucene</groupId>
    <artifactId>lucene-queryparser</artifactId>
    <version>9.4.2</version>
<groupId>org.apache.lucene</groupId>
    <artifactId>lucene-memory</artifactId>
    <version>9.4.2</version>
<groupId>org.apache.lucene</groupId>
    <artifactId>lucene-sandbox</artifactId>
    <version>9.4.2</version>
