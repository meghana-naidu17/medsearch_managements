$ErrorActionPreference = "Continue"
if (-not $env:MEDSEARCH_DB_PASSWORD) {
    Write-Host "[INFO] MEDSEARCH_DB_PASSWORD not detected in environment." -ForegroundColor Yellow
    Write-Host "[INFO] Starting MedSearch with high-performance embedded dataset store (Patients, Doctors, Appointments, Records preloaded)." -ForegroundColor Green
}
$cp = ".;lib/postgresql-42.7.11.jar"
java -cp $cp Main
