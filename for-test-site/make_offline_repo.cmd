@echo off

set MAVEN_SETTINGS=-s "D:\Program Files\apache-maven\conf\settings.xml"

mvn %MAVEN_SETTINGS% ^
 p2:site
