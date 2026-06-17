Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Push-Location guardian-ui
try {
    npm install
    npm start
}
finally {
    Pop-Location
}
