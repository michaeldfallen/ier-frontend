#!/bin/bash

thisDir="$( dirname "$0" )"
cd `pwd`

updateTemplate=$thisDir/scripts/update-template.sh
installSBT="$thisDir/scripts/install-sbt.sh"
sbt="$thisDir/scripts/bin/sbt/bin/sbt"

debugOpts="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9999"
repoOpts="-Dsbt.repository.config=$thisDir/project/repositories -Dsbt.override.build.repos=true"
jvmOpts="-XX:MaxPermSize=512M -Xms256M -Xmx512M -Xss1M"

if [ ! -f "$sbt" ]; then 
  echo "SBT not found. Installing SBT"
  echo `$installSBT` 
fi

export SBT_OPTS="$debugOpts $repoOpts $jvmOpts"

$updateTemplate
$sbt "$@"
