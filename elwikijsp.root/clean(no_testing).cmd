@echo off

mvn clean -Pwindows -Dmaven.test.skip=true  %* | tee Build.log
