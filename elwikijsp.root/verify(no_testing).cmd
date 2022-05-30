@echo off

call %~dp0\repositories_list.cmd
call %~dp0\..\maven_settings.cmd

mvn verify %MAVEN_SETTINGS% ^
 -Pwindows -Dmaven.test.skip=true  %* | tee Build.log
