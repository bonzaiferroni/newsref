#!/bin/bash

# Build the project
# ./gradlew :server:build

# Copy the necessary files to the server
scp -r server/build/libs/server-all.jar starfox@streetlight.ing:/home/starfox/server/
scp -r www/* starfox@streetlight.ing:/home/starfox/server/www/

# SSH into the server, kill the current process, and restart it
ssh starfox@streetlight.ing << 'ENDSSH'
  # Find and kill the current running process
  pid=$(pgrep -f 'java -jar /home/starfox/server/server-all.jar')
  if [ -n "$pid" ]; then
    kill $pid
  fi

  # Start the new process in a detached manner
  cd /home/starfox/server
  nohup /usr/bin/java -jar /home/starfox/server/server-all.jar > /dev/null 2>&1 &
ENDSSH