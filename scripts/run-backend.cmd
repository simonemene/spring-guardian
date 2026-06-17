@echo off
setlocal

echo Building guardian-server and required modules...
call mvn -pl guardian-server -am package -DskipTests
if errorlevel 1 exit /b %errorlevel%

echo Starting guardian-server on http://localhost:8080 ...
call mvn -pl guardian-server spring-boot:run
