#!/bin/sh
#

mvn clean  -Dmaven.test.skip=true  $* | tee Build.log
