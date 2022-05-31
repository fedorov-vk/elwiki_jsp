@echo off

call %~dp0\..\utilities\maven_settings.cmd

mvn %MAVEN_SETTINGS% ^
 p2:site | tee Build.log
