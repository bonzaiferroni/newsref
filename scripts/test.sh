#!/bin/bash
./gradlew -p "$(pwd)" :krawly:run --console=plain --args="$*"