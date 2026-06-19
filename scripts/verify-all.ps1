Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Write-Host "[Spring Guardian] Backend + core + CLI + server tests" -ForegroundColor Cyan
mvn clean install

Write-Host "[Spring Guardian] Angular dependencies" -ForegroundColor Cyan
Push-Location guardian-ui
try {
    npm ci
    npm run build
}
finally {
    Pop-Location
}

Write-Host "[Spring Guardian] Verification completed" -ForegroundColor Green
