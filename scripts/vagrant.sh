#!/bin/bash
sudo adduser vagrant root
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10
echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | sudo tee /etc/apt/sources.list.d/10gen.list
sudo apt-get update
sudo apt-get install maven openjdk-7-jdk mongodb-10gen -y
sudo touch /var/log/ier/ier-api.log
chmod 777 /var/log/ier/ier-api.log

export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-amd64/

cd /ier-api
mvn clean package
