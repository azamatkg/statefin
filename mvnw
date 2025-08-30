#!/bin/sh
# Maven wrapper script for Unix-like systems

MAVEN_WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES=".mvn/wrapper/maven-wrapper.properties"

# Download Maven wrapper if it doesn't exist
if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
    echo "Maven wrapper not found. Using system Maven..."
    exec mvn "$@"
fi

# Execute Maven wrapper
exec java -jar "$MAVEN_WRAPPER_JAR" "$@"