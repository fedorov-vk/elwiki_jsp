@echo off

call %~dp0\..\utilities\maven_settings.cmd

mvn %MAVEN_SETTINGS% ^
 jetty:run %* | tee Build.log
