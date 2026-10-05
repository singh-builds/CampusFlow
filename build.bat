@echo off
REM Compiles CampusFlow, builds CampusFlow.war and copies it into Tomcat.
cd /d "%~dp0"
if "%CATALINA_HOME%"=="" (echo Set CATALINA_HOME to your Tomcat folder first & exit /b 1)
if not exist config\db.properties (echo Create config\db.properties first - copy config\db.properties.template & exit /b 1)
if not exist lib\mysql-connector-j-9.1.0.jar (echo lib folder is empty. Run get-libs.bat first & exit /b 1)

if exist build rmdir /s /q build
mkdir build\WEB-INF\classes
mkdir build\WEB-INF\lib
xcopy /e /i /y /q src\main\webapp build >nul
copy /y lib\*.jar build\WEB-INF\lib\ >nul
copy /y config\db.properties build\WEB-INF\classes\db.properties >nul
dir /s /b src\main\java\*.java > sources.txt
javac -encoding UTF-8 -cp "lib\*;%CATALINA_HOME%\lib\*" -d build\WEB-INF\classes @sources.txt
if errorlevel 1 (del sources.txt & echo COMPILE FAILED & exit /b 1)
del sources.txt
jar cf CampusFlow.war -C build .
if exist "%CATALINA_HOME%\webapps\CampusFlow" rmdir /s /q "%CATALINA_HOME%\webapps\CampusFlow"
copy /y CampusFlow.war "%CATALINA_HOME%\webapps\" >nul
echo BUILD OK - CampusFlow.war copied to Tomcat
