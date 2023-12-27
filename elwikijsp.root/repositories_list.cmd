@echo off
::
:: This file defines the location of p2 repositories, needed to build ElWiki.
::
set PLATFORM_REPO=%USERPROFILE%/p2/eclipse/releases/2022-12

set P2_REPO_PLACE=%~dp0\..\p2_repositories
:: APACHETOOLS_REPO - probably not used anymore.
set CUSTOM_REPO01=%P2_REPO_PLACE%\apache-tools\target\repository
set CUSTOM_REPO02=%P2_REPO_PLACE%\custom-p2-site\target\repository
set CUSTOM_REPO03=%P2_REPO_PLACE%\additional-artifacts\target\repository
set CUSTOM_REPO04=%P2_REPO_PLACE%\for-test-site\target\repository

set "CUSTOM_REPO01=%CUSTOM_REPO01:\=/%"
set "CUSTOM_REPO02=%CUSTOM_REPO02:\=/%"
set "CUSTOM_REPO03=%CUSTOM_REPO03:\=/%"
set "CUSTOM_REPO04=%CUSTOM_REPO04:\=/%"
set JAVADOC=%~dp0\..\javadoc
