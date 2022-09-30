#!/bin/sh
#

. ./repositories_list.sh
#mvn install        -Dmaven.test.skip=true  $* | tee Build.log
mvn install -Plinux -Dmaven.test.skip=true  $* | tee Build.log
