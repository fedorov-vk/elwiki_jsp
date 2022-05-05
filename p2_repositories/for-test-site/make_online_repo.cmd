@echo off

set MAVEN_SETTINGS=-s "D:\Program Files\apache-maven\conf\settings.xml"

mvn %MAVEN_SETTINGS% ^
 jetty:run %* | tee Build.log
