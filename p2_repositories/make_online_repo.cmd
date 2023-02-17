@echo off

mvn jetty:run %* | tee Build.log
