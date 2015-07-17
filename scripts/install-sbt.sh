#!/bin/bash

thisDir="$( dirname "$0")"
sbtVersion="0.13.5"
sbtdir="$thisDir/bin"
sbtsource="http://dl.bintray.com/sbt/native-packages/sbt/$sbtVersion"
sbtartifact="sbt-$sbtVersion"
sbtextension=".tgz"
sbtcache="download-cache"

mkdir -p "$sbtdir/$sbtcache"

sbtzip=$sbtartifact$sbtextension
dest="$sbtdir/$sbtcache/$sbtzip"

if [ ! -f "$dest" ]; then
  echo "Downloading $sbtsource/$sbtzip to $dest"
  curl -# -L -o "$dest" "$sbtsource/$sbtzip"
fi
if [ ! -f "$sbtdir/$sbtartifact" ]; then
  echo "Unzipping to $sbtdir"
  tar -xf $dest -C $sbtdir 
fi

echo "SBT is installed"
