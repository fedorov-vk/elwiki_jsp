@echo off

call %~dp0\repositories_list.cmd
call %~dp0\..\utilities\maven_settings.cmd

REM -Dmaven.test.skip=true
mvn install %MAVEN_SETTINGS% ^
 -Pwindows -rf :tests %* | tee Build.log
