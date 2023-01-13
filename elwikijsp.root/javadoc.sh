#!/bin/sh
#
. ./repositories_list.sh
mvn javadoc:javadoc -Dmaven.test.skip=true -Plinux $* | tee Build.log
