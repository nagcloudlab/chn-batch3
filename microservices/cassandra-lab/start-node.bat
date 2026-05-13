@echo off
REM
REM Start a single Cassandra node back (after stop-node.bat)
REM Usage: start-node.bat 1  or  start-node.bat 2
REM

setlocal
set CASSANDRA_LAB=%~dp0
set NODE_NUM=%1

if "%NODE_NUM%"=="" (
    echo Usage: start-node.bat ^<1^|2^>
    exit /b 1
)

if "%NODE_NUM%"=="1" set PORT=9042
if "%NODE_NUM%"=="2" set PORT=9043

echo Starting Node %NODE_NUM% on port %PORT%...
set CASSANDRA_HOME=%CASSANDRA_LAB%cassandra-node-%NODE_NUM%
start "Cassandra-Node%NODE_NUM%" /min "%CASSANDRA_LAB%cassandra-node-%NODE_NUM%\bin\cassandra.bat"

echo Waiting for Node %NODE_NUM%...
timeout /t 20 /nobreak >nul
echo Node %NODE_NUM% started.
echo Check: cassandra-node-1\bin\nodetool -p 7199 status

endlocal
