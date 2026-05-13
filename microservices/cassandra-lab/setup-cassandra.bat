@echo off
REM
REM Download and setup 2-node Cassandra 5.0 cluster for Windows
REM Usage: setup-cassandra.bat
REM

setlocal enabledelayedexpansion

set CASSANDRA_LAB=%~dp0
set CASSANDRA_VERSION=5.0.4
set TARBALL=apache-cassandra-%CASSANDRA_VERSION%-bin.tar.gz
set DOWNLOAD_URL=https://archive.apache.org/dist/cassandra/%CASSANDRA_VERSION%/%TARBALL%

echo ================================================
echo   Cassandra 2-Node Cluster Setup (v%CASSANDRA_VERSION%)
echo ================================================

java -version 2>nul
if errorlevel 1 (
    echo Java 17+ is required.
    exit /b 1
)

cd /d "%CASSANDRA_LAB%"

if exist "cassandra-node-1" if exist "cassandra-node-2" (
    echo Both nodes already exist. Skipping download.
    goto :done
)

if not exist "%TARBALL%" (
    echo Downloading Cassandra %CASSANDRA_VERSION%...
    curl -L -O "%DOWNLOAD_URL%"
    if errorlevel 1 (
        echo Download failed.
        exit /b 1
    )
)

for %%N in (1 2) do (
    if exist "cassandra-node-%%N" (
        echo   cassandra-node-%%N already exists.
    ) else (
        echo   Setting up Node %%N...
        tar -xzf "%TARBALL%"
        rename "apache-cassandra-%CASSANDRA_VERSION%" "cassandra-node-%%N"

        set "YAML=cassandra-node-%%N\conf\cassandra.yaml"
        set "ENV_BAT=cassandra-node-%%N\conf\cassandra-env.ps1"

        if "%%N"=="1" (set NPORT=9042& set SPORT=7000& set JPORT=7199)
        if "%%N"=="2" (set NPORT=9043& set SPORT=7001& set JPORT=7299)

        powershell -Command "(Get-Content '!YAML!') -replace '/var/lib/cassandra/data', '%CASSANDRA_LAB%cassandra-node-%%N\data\data' -replace '/var/lib/cassandra/commitlog', '%CASSANDRA_LAB%cassandra-node-%%N\data\commitlog' -replace '/var/lib/cassandra/saved_caches', '%CASSANDRA_LAB%cassandra-node-%%N\data\saved_caches' -replace '/var/lib/cassandra/hints', '%CASSANDRA_LAB%cassandra-node-%%N\data\hints' -replace \"cluster_name: 'Test Cluster'\", \"cluster_name: 'npci-local-cluster'\" -replace 'native_transport_port: 9042', 'native_transport_port: !NPORT!' -replace 'storage_port: 7000', 'storage_port: !SPORT!' | Set-Content '!YAML!'"

        mkdir "cassandra-node-%%N\data\data" 2>nul
        mkdir "cassandra-node-%%N\data\commitlog" 2>nul
        mkdir "cassandra-node-%%N\data\saved_caches" 2>nul
        mkdir "cassandra-node-%%N\data\hints" 2>nul
        mkdir "cassandra-node-%%N\logs" 2>nul

        REM Reduce heap: create/append to jvm-server.options
        echo. >> "cassandra-node-%%N\conf\jvm-server.options"
        echo # Local dev reduced heap >> "cassandra-node-%%N\conf\jvm-server.options"
        echo -Xms256M >> "cassandra-node-%%N\conf\jvm-server.options"
        echo -Xmx256M >> "cassandra-node-%%N\conf\jvm-server.options"
        echo -Xmn128M >> "cassandra-node-%%N\conf\jvm-server.options"

        echo   Node %%N: native=!NPORT!, storage=!SPORT!, heap=256M
    )
)

del /f "%TARBALL%" 2>nul

:done
echo.
echo ================================================
echo   Setup Complete! (Cassandra %CASSANDRA_VERSION%)
echo ================================================
echo   Node 1 (seed) : localhost:9042
echo   Node 2        : localhost:9043
echo.
echo   Start : start-cassandra.bat
echo   Stop  : stop-cassandra.bat
echo   CQL   : cassandra-node-1\bin\cqlsh localhost 9042
echo ================================================

endlocal
