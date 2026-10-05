@echo off
REM One click: build -> deploy to Tomcat -> start Tomcat -> start Node.js
cd /d "%~dp0"
call build.bat
if errorlevel 1 exit /b 1
call "%CATALINA_HOME%\bin\startup.bat"
echo.
echo Wait about 10 seconds for Tomcat, then open:
echo   App (Java):        http://localhost:8080/CampusFlow/
echo   Queue Board (Node): http://localhost:3000/
cd node-api
node server.js
