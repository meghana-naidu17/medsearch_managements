@echo off
if "%MEDSEARCH_DB_PASSWORD%"=="" (
  echo [INFO] MEDSEARCH_DB_PASSWORD is not set. Running with integrated dataset store.
)
java -cp ".;lib\postgresql-42.7.11.jar" Main
