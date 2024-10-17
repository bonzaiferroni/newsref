#!/bin/bash

# Fail on error
set -e

# Build the Gradle module
./gradlew :krawly:build

# Generate the JAR
./gradlew :krawly:jar