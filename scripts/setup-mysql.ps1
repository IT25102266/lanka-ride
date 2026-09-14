# One-time setup for local MySQL80 (not Docker).
# Usage (PowerShell):
#   .\scripts\setup-mysql.ps1 -RootPassword 'YOUR_MYSQL_ROOT_PASSWORD'

param(
    [Parameter(Mandatory = $true)]
    [string]$RootPassword
)

$ErrorActionPreference = "Stop"
$mysql = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if (-not (Test-Path $mysql)) {
    throw "mysql.exe not found at $mysql"
}

$sqlFile = Join-Path $PSScriptRoot "init-mysql.sql"
if (-not (Test-Path $sqlFile)) {
    throw "Missing $sqlFile"
}

Write-Host "Creating database/user lanka_ride / lankaride ..."
& $mysql -u root --password=$RootPassword -h 127.0.0.1 --protocol=tcp -e "source $($sqlFile -replace '\\','/')"
if ($LASTEXITCODE -ne 0) {
    throw "MySQL setup failed (exit $LASTEXITCODE). Check the root password."
}

Write-Host "Verifying app login..."
& $mysql -u lankaride --password=lankaride -h 127.0.0.1 --protocol=tcp -e "USE lanka_ride; SELECT DATABASE() AS db;"
if ($LASTEXITCODE -ne 0) {
    throw "App user verification failed."
}

Write-Host "MySQL ready for Lanka Ride (database: lanka_ride, user: lankaride)."
