#!/bin/sh
mvn jetty:run $* | tee Build.log
