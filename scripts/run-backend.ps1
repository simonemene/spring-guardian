Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Write-Host "Starting guardian-server with guardian-core in the same Maven reactor..." -ForegroundColor Cyan
mvn -pl guardian-server -am spring-boot:run
