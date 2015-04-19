#!/bin/sh

JAR_NAME=Kiqo-1.0-shaded

# the debian-specific command "update-alternatives" lists all installed java commands
java8_path=$(update-alternatives --list java | grep java-8)

if [ $java8_path ]
    then
        $java8_path -jar $JAR_NAME.jar
    else echo "Java 8 does not appear to be installed on this machine.
Please install Java 8 from http://java.com/en/download/."
fi

return 0

