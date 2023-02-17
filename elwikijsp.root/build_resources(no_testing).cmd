@echo off

call %~dp0\repositories_list.cmd

  mvn verify -Pwindows -Dmaven.test.skip=true -pl :org.elwiki.resources -am  %* | tee Build.log
::mvn verify -Pwindows -Dmaven.test.skip=true -pl :org.elwiki.resources      %* | tee Build.log
