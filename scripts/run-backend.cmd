@echo off
setlocal

echo Starting guardian-server with guardian-core in the same Maven reactor...
call mvn -pl guardian-server -am spring-boot:run
