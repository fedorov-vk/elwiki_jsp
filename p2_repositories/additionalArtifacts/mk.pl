while(<DATA>) {
    chomp;
    next if($_ eq '');

#    if( /^(.+?\w)-(\d+?\.\d+?\.\d+?).jar/ ) { print "$1\n$2\n"; }
print <<EOL;
				<artifact><id>$_</id><source>true</source></artifact>
EOL
}

__DATA__
org.apache.taglibs:taglibs-standard-impl:1.2.1
org.apache.taglibs:taglibs-standard-compat:1.2.1
org.apache.taglibs:taglibs-standard-spec:1.2.1
org.apache.taglibs:taglibs-standard-jstlel:1.2.1
org.apache.xalan:xalan:2.7.1
org.apache.xml:serializer:2.7.1
