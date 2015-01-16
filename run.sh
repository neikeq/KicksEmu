#!/bin/sh

java -classpath lib/commons-dbcp2-2.0.1.jar:lib/commons-pool2-2.2.jar:lib/commons-logging-1.1.3.jar:lib/mysql-connector-java-5.1.30-bin.jar:lib/netty-all-4.0.24.Final.jar:lib/opencsv-3.1.jar:out/production/KicksEmu com/neikeq/kicksemu/KicksEmu $1

read -p 'Press enter to continue...' key
