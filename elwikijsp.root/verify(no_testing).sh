#!/bin/sh
#
. ./repositories_list.sh
#mvn install        -Dmaven.test.skip=true  $* | tee Build.log
mvn verify -Plinux -Dmaven.test.skip=true $* | tee Build.log
