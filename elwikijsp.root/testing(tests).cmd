@echo off

call %~dp0\repositories_list.cmd

REM -Dmaven.test.skip=true
mvn install -Pwindows -rf :tests %* | tee Build.log
