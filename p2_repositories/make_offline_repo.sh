#!/bin/sh
mvn p2:site $* | tee Build.log
