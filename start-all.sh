#!/usr/bin/env bash
# One command: build -> deploy to Tomcat -> start Tomcat -> start Node.js
cd "$(dirname "$0")"
./build.sh || exit 1
"$CATALINA_HOME/bin/startup.sh"
echo "App (Java):         http://localhost:8080/CampusFlow/"
echo "Queue Board (Node): http://localhost:3000/"
cd node-api && node server.js
