@echo off

call repositories_list.cmd
set MAVEN_SETTINGS=-s "D:\Program Files\apache-maven\conf\settings.xml"

mvn verify %MAVEN_SETTINGS% ^
 -Pwindows -Dmaven.test.skip=true  %* | tee Build.log
