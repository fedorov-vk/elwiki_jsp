while(<DATA>) {
    chomp;
    next if($_ eq '');

#    if( /^(.+?\w)-(\d+?\.\d+?\.\d+?).jar/ ) { print "$1\n$2\n"; }
print <<EOL;
				<artifact><id>$_</id><source>true</source></artifact>
EOL
}

__DATA__

org.mockito:mockito-core:3.7.0
