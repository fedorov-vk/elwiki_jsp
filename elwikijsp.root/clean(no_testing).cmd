@echo off

call %~dp0\..\utilities\maven_settings.cmd

mvn clean %MAVEN_SETTINGS% ^
 -Pwindows -Dmaven.test.skip=true  %* | tee Build.log
