@echo off

call %~dp0\repositories_list.cmd
call %~dp0\..\utilities\maven_settings.cmd

  mvn verify %MAVEN_SETTINGS% -Pwindows -Dmaven.test.skip=true -pl :org.elwiki.resources -am  %* | tee Build.log
::mvn verify %MAVEN_SETTINGS% -Pwindows -Dmaven.test.skip=true -pl :org.elwiki.resources      %* | tee Build.log
