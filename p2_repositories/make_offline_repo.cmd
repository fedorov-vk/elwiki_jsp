@echo off

call %~dp0\..\maven_settings.cmd

mvn %MAVEN_SETTINGS% ^
 p2:site | tee Build.log
