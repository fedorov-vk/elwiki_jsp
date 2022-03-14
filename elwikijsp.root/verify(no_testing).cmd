@echo off

mvn verify -Pwindows -Dmaven.test.skip=true  %* | tee Build.log
