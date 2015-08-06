#!/bin/bash

JAR_PATH=./${project.artifactId}-${project.version}.jar

# the debian-specific command "update-alternatives" lists all installed java commands
java8_path=$(update-alternatives --list java | grep java-8)

if [ $java8_path ]
    then
        $java8_path -jar $JAR_PATH
    else echo "Java 8 does not appear to be installed on this machine.
Please install Java 8 from http://java.com/en/download/."
fi
