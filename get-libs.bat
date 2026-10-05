@echo off
REM Downloads the 7 jar files the project needs into the lib folder (needs internet, Windows 10+).
cd /d "%~dp0"
if not exist lib mkdir lib
set M=https://repo1.maven.org/maven2
curl -fL -o lib\mysql-connector-j-9.1.0.jar %M%/com/mysql/mysql-connector-j/9.1.0/mysql-connector-j-9.1.0.jar || goto fail
curl -fL -o lib\jakarta.servlet.jsp.jstl-api-3.0.0.jar %M%/jakarta/servlet/jsp/jstl/jakarta.servlet.jsp.jstl-api/3.0.0/jakarta.servlet.jsp.jstl-api-3.0.0.jar || goto fail
curl -fL -o lib\jakarta.servlet.jsp.jstl-3.0.1.jar %M%/org/glassfish/web/jakarta.servlet.jsp.jstl/3.0.1/jakarta.servlet.jsp.jstl-3.0.1.jar || goto fail
curl -fL -o lib\jakarta.mail-api-2.1.3.jar %M%/jakarta/mail/jakarta.mail-api/2.1.3/jakarta.mail-api-2.1.3.jar || goto fail
curl -fL -o lib\angus-mail-2.0.3.jar %M%/org/eclipse/angus/angus-mail/2.0.3/angus-mail-2.0.3.jar || goto fail
curl -fL -o lib\jakarta.activation-api-2.1.3.jar %M%/jakarta/activation/jakarta.activation-api/2.1.3/jakarta.activation-api-2.1.3.jar || goto fail
curl -fL -o lib\angus-activation-2.0.2.jar %M%/org/eclipse/angus/angus-activation/2.0.2/angus-activation-2.0.2.jar || goto fail
echo ALL LIBRARIES DOWNLOADED
exit /b 0
:fail
echo DOWNLOAD FAILED - check your internet and run again
exit /b 1
