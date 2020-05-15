#!/bin/sh
#

mvn install  -Dmaven.test.skip=true  $* | tee Build.log
