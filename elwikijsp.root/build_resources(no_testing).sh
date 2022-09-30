#!/bin/sh
#

. ./repositories_list.sh
mvn install -Plinux -Dmaven.test.skip=true -pl :org.elwiki.resources -am  $* | tee Build.log
