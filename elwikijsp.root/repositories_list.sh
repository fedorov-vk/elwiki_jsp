#
# This file defines the location of p2 repositories, needed to build ElWiki.
#
export PLATFORM_REPO=/repo/eclipse/releases/2022-12

P2_REPO_PLACE=$PWD/../p2_repositories
# APACHETOOLS_REPO - probably not used anymore.
export APACHETOOLS_REPO=$P2_REPO_PLACE/apache-tools/target/repository
export CUSTOM_REPO01=$P2_REPO_PLACE/custom-p2-site/target/repository
export CUSTOM_REPO02=$P2_REPO_PLACE/additional-artifacts/target/repository
export CUSTOM_REPO03=$P2_REPO_PLACE/from-eclipse/target/repository
export CUSTOM_REPO04=$P2_REPO_PLACE/for-test-site/target/repository
export JAVADOC=$PWD/../javadoc/
