Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Write-Host "Building guardian-server and required modules..." -ForegroundColor Cyan
mvn -pl guardian-server -am package -DskipTests
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

Write-Host "Starting guardian-server on http://localhost:8080 ..." -ForegroundColor Cyan
mvn -pl guardian-server spring-boot:run
