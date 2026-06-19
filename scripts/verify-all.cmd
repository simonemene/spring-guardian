@echo off
setlocal
powershell -ExecutionPolicy Bypass -File "%~dp0verify-all.ps1"
endlocal
