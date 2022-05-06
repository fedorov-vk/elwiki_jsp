@echo off

call repositories_list.cmd
set MAVEN_SETTINGS=-s "D:\Program Files\apache-maven\conf\settings.xml"

REM -Dmaven.test.skip=true
mvn install %MAVEN_SETTINGS% ^
 -Pwindows %* | tee Build.log
