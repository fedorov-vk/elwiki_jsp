@echo off

call %~dp0\..\maven_settings.cmd

mvn %MAVEN_SETTINGS% ^
 jetty:run %* | tee Build.log
