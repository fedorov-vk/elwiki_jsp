#!/bin/sh
#

. ./repositories_list.sh
mvn clean  -Dmaven.test.skip=true  $* | tee Build.log
