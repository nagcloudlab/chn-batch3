@echo off
REM
REM Start 2-node Cassandra cluster on Windows
REM Run from the cassandra-lab folder: start-cassandra.bat
REM

setlocal
cd /d "%~dp0"

echo ================================================
echo   Starting 2-Node Cassandra Cluster
echo ================================================

if not exist "cassandra-node-1" (
    echo Nodes not found. Run setup-cassandra.bat first.
    exit /b 1
)

echo.
echo [1/2] Starting Node 1 (seed) on port 9042...
set CASSANDRA_HOME=%cd%\cassandra-node-1
start "Cassandra-Node1" /min cmd /c "cd /d %cd%\cassandra-node-1 && bin\cassandra.bat"

echo   Waiting for Node 1 (this may take 1-2 minutes)...
:wait_node1
timeout /t 5 /nobreak >nul
cassandra-node-1\bin\cqlsh.bat localhost 9042 -e "DESCRIBE keyspaces;" >nul 2>&1
if errorlevel 1 (
    echo|set /p=.
    goto :wait_node1
)
echo.
echo   Node 1 is UP!

echo.
echo [2/2] Starting Node 2 on port 9043...
set CASSANDRA_HOME=%cd%\cassandra-node-2
start "Cassandra-Node2" /min cmd /c "cd /d %cd%\cassandra-node-2 && bin\cassandra.bat"
echo   Waiting for Node 2 to join...
timeout /t 25 /nobreak >nul
echo   Node 2 started.

echo.
echo Cluster status:
cassandra-node-1\bin\nodetool.bat -p 7199 status

echo.
echo ================================================
echo   Cluster is UP!
echo ================================================
echo   Node 1 (seed) : localhost:9042
echo   Node 2        : localhost:9043
echo.
echo   CQL shell : cassandra-node-1\bin\cqlsh localhost 9042
echo   Init schema: cassandra-node-1\bin\cqlsh -f init-schema.cql
echo   Ring       : cassandra-node-1\bin\nodetool -p 7199 ring
echo   Status     : cassandra-node-1\bin\nodetool -p 7199 status
echo ================================================

endlocal
