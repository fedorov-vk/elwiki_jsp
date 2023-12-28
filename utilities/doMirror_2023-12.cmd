@echo off
REM HOW-TO: https://wiki.eclipse.org/Equinox_p2_Repository_Mirroring
REM Creates a local copy of the Eclipse p2 repository.
REM The repository URL desines SRC, for example:
REM   https://download.eclipse.org/releases/2019-03/201902081000/
REM The placement directory defines DST. (at running directory - will be created %DST%)
REM ECLIPSE - determines the placement of the starter of the installed Eclipse.
REM OPTS - defines additional startup parameters.
 
set ECLIPSE=D:\PROGRA~2\Eclipse\2023-12\eclipse_rcp\eclipse.exe
set OPTS=-nosplash -verbose
set SRC=https://download.eclipse.org/releases/2023-12
set DST=2023-12

echo Get from: %SRC%
echo   Put to: %DST%

%ECLIPSE% %OPTS% -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source %SRC%  -destination %DST%
%ECLIPSE% %OPTS% -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source %SRC%  -destination %DST%
