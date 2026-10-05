#!/usr/bin/env bash
# Downloads the 7 jar files the project needs into lib/ (needs internet).
set -e
cd "$(dirname "$0")"; mkdir -p lib
M=https://repo1.maven.org/maven2
get() { curl -fL -o "lib/$(basename "$1")" "$M/$1"; }
get com/mysql/mysql-connector-j/9.1.0/mysql-connector-j-9.1.0.jar
get jakarta/servlet/jsp/jstl/jakarta.servlet.jsp.jstl-api/3.0.0/jakarta.servlet.jsp.jstl-api-3.0.0.jar
get org/glassfish/web/jakarta.servlet.jsp.jstl/3.0.1/jakarta.servlet.jsp.jstl-3.0.1.jar
get jakarta/mail/jakarta.mail-api/2.1.3/jakarta.mail-api-2.1.3.jar
get org/eclipse/angus/angus-mail/2.0.3/angus-mail-2.0.3.jar
get jakarta/activation/jakarta.activation-api/2.1.3/jakarta.activation-api-2.1.3.jar
get org/eclipse/angus/angus-activation/2.0.2/angus-activation-2.0.2.jar
echo "ALL LIBRARIES DOWNLOADED"
