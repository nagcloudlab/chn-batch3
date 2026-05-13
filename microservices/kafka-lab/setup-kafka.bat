@echo off
REM
REM Download and setup 3-broker Kafka cluster + Kafka UI for Windows
REM Usage: setup-kafka.bat
REM

setlocal enabledelayedexpansion

set KAFKA_LAB=%~dp0
set KAFKA_VERSION=3.9.2
set SCALA_VERSION=2.13
set KAFKA_TARBALL=kafka_%SCALA_VERSION%-%KAFKA_VERSION%.tgz
set KAFKA_DOWNLOAD=https://archive.apache.org/dist/kafka/%KAFKA_VERSION%/%KAFKA_TARBALL%
set KAFKA_UI_VERSION=0.7.2
set KAFKA_UI_JAR=kafka-ui-api-v%KAFKA_UI_VERSION%.jar
set KAFKA_UI_DOWNLOAD=https://github.com/provectus/kafka-ui/releases/download/v%KAFKA_UI_VERSION%/%KAFKA_UI_JAR%

echo ================================================
echo   Kafka Cluster Setup
echo   Kafka %KAFKA_VERSION% + Kafka UI %KAFKA_UI_VERSION%
echo ================================================

REM Check Java
java -version 2>nul
if errorlevel 1 (
    echo Java is required. Please install Java 17+.
    exit /b 1
)

cd /d "%KAFKA_LAB%"

REM Download Kafka
if exist "kafka-101" if exist "kafka-102" if exist "kafka-103" (
    echo All 3 broker directories already exist. Skipping download.
    goto :setup_ui
)

if not exist "%KAFKA_TARBALL%" (
    echo [1/4] Downloading Kafka %KAFKA_VERSION%...
    curl -L -O "%KAFKA_DOWNLOAD%"
    if errorlevel 1 (
        echo Download failed.
        exit /b 1
    )
)

REM Extract 3 copies
for %%B in (101 102 103) do (
    if exist "kafka-%%B" (
        echo   kafka-%%B already exists.
    ) else (
        echo   Extracting kafka-%%B...
        tar -xzf "%KAFKA_TARBALL%"
        rename "kafka_%SCALA_VERSION%-%KAFKA_VERSION%" "kafka-%%B"

        set "CONF=kafka-%%B\config\server.properties"

        if "%%B"=="101" set PORT=9092
        if "%%B"=="102" set PORT=9093
        if "%%B"=="103" set PORT=9094

        powershell -Command "(Get-Content '!CONF!') -replace 'broker.id=0', 'broker.id=%%B' | Set-Content '!CONF!'"
        powershell -Command "(Get-Content '!CONF!') -replace '#listeners=PLAINTEXT://:9092', 'listeners=PLAINTEXT://:!PORT!' | Set-Content '!CONF!'"
        powershell -Command "(Get-Content '!CONF!') -replace '#advertised.listeners=PLAINTEXT://your.host.name:9092', 'advertised.listeners=PLAINTEXT://localhost:!PORT!' | Set-Content '!CONF!'"
        powershell -Command "(Get-Content '!CONF!') -replace 'log.dirs=/tmp/kafka-logs', 'log.dirs=C:/tmp/kafka-logs-%%B' | Set-Content '!CONF!'"

        echo   kafka-%%B: broker.id=%%B, port=!PORT!, advertised=localhost:!PORT!
    )
)

del /f "%KAFKA_TARBALL%" 2>nul

:setup_ui
REM Download Kafka UI
if not exist "kafka-ui" mkdir kafka-ui

if exist "kafka-ui\%KAFKA_UI_JAR%" (
    echo Kafka UI jar already exists.
) else (
    echo [2/4] Downloading Kafka UI %KAFKA_UI_VERSION%...
    curl -L -o "kafka-ui\%KAFKA_UI_JAR%" "%KAFKA_UI_DOWNLOAD%"
)

REM Create Kafka UI config
if not exist "kafka-ui\application.yml" (
    echo [3/4] Creating Kafka UI config...
    (
        echo kafka:
        echo   clusters:
        echo     - name: npci-local-cluster
        echo       bootstrapServers: localhost:9092,localhost:9093,localhost:9094
        echo dynamic.config.enabled: true
    ) > "kafka-ui\application.yml"
)

echo.
echo ================================================
echo   Setup Complete!
echo ================================================
echo.
echo   kafka-101 : broker.id=101, port 9092
echo   kafka-102 : broker.id=102, port 9093
echo   kafka-103 : broker.id=103, port 9094
echo   kafka-ui  : port 8080
echo.
echo   Start: start-kafka.bat
echo   Stop:  stop-kafka.bat
echo ================================================

endlocal
