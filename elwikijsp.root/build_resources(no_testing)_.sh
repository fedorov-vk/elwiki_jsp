#!/bin/sh
#

mvn install -pl :org.elwiki.resources -am  -Dmaven.test.skip=true  $* | tee Build.log
