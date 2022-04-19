#!/bin/sh
#

#mvn install        -Dmaven.test.skip=true  $* | tee Build.log
mvn install -Plinux -rf :tests $* | tee Build.log
