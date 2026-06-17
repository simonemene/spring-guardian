@echo off
setlocal
cd guardian-ui
call npm install
if errorlevel 1 exit /b %errorlevel%
call npm start
