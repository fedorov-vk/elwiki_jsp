#!/bin/sh
# Techinfo see: https://phauer.com/2015/offline-copy-mirror-eclipse-p2-repository/
# You should set the SRC, for example:
#   https://download.eclipse.org/releases/2019-03/201902081000/?d
#
# NOTE: The placement directory is obtained by clipping "https://download.eclipse.org/releases/"
# The destination directory is created automatically.
#

SRC="https://download.eclipse.org/releases/2023-12"
DST=2023-12

ECLIPSE=/home/vfedorov/local/Eclipse_photon/eclipse_pde/eclipse
#ECLIPSE=/home/vfedorov/local/Eclipse_neon_Release/eclipse_jee_46/eclipse

OPTS='-nosplash -verbose'


get_repo() {
  SRC=$1
  DST=$2
  $ECLIPSE $OPTS -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source $SRC  -destination $DST
  $ECLIPSE $OPTS -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source $SRC  -destination $DST
}


echo "Get from: "$SRC
echo "  Put to: "$DST
get_repo  $SRC  $DST
