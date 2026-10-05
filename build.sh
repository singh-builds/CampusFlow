#!/usr/bin/env bash
# Compiles CampusFlow, builds CampusFlow.war and copies it into Tomcat.
set -e
cd "$(dirname "$0")"
: "${CATALINA_HOME:?Set CATALINA_HOME to your Tomcat folder first}"
[ -f config/db.properties ] || { echo "Create config/db.properties first (copy config/db.properties.template)"; exit 1; }
ls lib/*.jar >/dev/null 2>&1 || { echo "lib/ is empty. Run ./get-libs.sh first"; exit 1; }

rm -rf build && mkdir -p build/WEB-INF/classes build/WEB-INF/lib
cp -r src/main/webapp/. build/
cp lib/*.jar build/WEB-INF/lib/
cp config/db.properties build/WEB-INF/classes/db.properties
javac -encoding UTF-8 -cp "lib/*:$CATALINA_HOME/lib/*" -d build/WEB-INF/classes $(find src/main/java -name "*.java")
(cd build && jar cf ../CampusFlow.war .)
rm -rf "$CATALINA_HOME/webapps/CampusFlow"
cp CampusFlow.war "$CATALINA_HOME/webapps/"
echo "BUILD OK -> $CATALINA_HOME/webapps/CampusFlow.war"
