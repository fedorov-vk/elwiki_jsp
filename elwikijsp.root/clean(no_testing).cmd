@echo off

set MAVEN_SETTINGS=-s D:/PROGRA~2/apache-maven/conf/settings.xml

mvn clean %MAVEN_SETTINGS% ^
 -Pwindows -Dmaven.test.skip=true  %* | tee Build.log
