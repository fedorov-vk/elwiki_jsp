@echo off

call %~dp0\repositories_list.cmd

mvn verify -Pwindows -Dmaven.test.skip=true  %* | tee Build.log
