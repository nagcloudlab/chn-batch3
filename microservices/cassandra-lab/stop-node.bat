@echo off
REM
REM Stop a single Cassandra node (for CL demos)
REM Usage: stop-node.bat 1  or  stop-node.bat 2
REM

set NODE_NUM=%1

if "%NODE_NUM%"=="" (
    echo Usage: stop-node.bat ^<1^|2^>
    exit /b 1
)

if "%NODE_NUM%"=="1" set PORT=9042
if "%NODE_NUM%"=="2" set PORT=9043

echo Stopping Node %NODE_NUM% (port %PORT%)...
for /f "tokens=5" %%p in ('netstat -aon ^| findstr :%PORT% ^| findstr LISTENING') do taskkill /PID %%p /F >nul 2>&1

echo Node %NODE_NUM% stopped.
echo.
echo CL Demo (RF=2, 1 node down):
echo   CONSISTENCY ONE;  =^> WORKS (1 of 1 available)
echo   CONSISTENCY ALL;  =^> FAILS (only 1 of 2 nodes available)
