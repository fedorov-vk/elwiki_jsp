@echo off

call %~dp0\repositories_list.cmd

REM -Dmaven.test.skip=true
mvn install -Pwindows %* | tee Build.log
