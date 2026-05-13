@echo off
REM
REM Stop 2-node Cassandra cluster on Windows
REM

echo Stopping Cassandra Cluster...

echo   Stopping Node 2 (port 9043)...
for /f "tokens=5" %%p in ('netstat -aon ^| findstr :9043 ^| findstr LISTENING') do taskkill /PID %%p /F >nul 2>&1

echo   Stopping Node 1 (port 9042)...
for /f "tokens=5" %%p in ('netstat -aon ^| findstr :9042 ^| findstr LISTENING') do taskkill /PID %%p /F >nul 2>&1

REM Also kill by process name as fallback
taskkill /F /IM java.exe /FI "WINDOWTITLE eq Cassandra-Node*" >nul 2>&1

echo.
echo Cluster is DOWN.
