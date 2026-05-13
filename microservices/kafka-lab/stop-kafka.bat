@echo off
REM
REM Stop Kafka cluster: Kafka UI + Brokers + ZooKeeper
REM

echo Stopping Kafka cluster...

echo   Stopping Kafka UI...
for /f "tokens=5" %%p in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do taskkill /PID %%p /F >nul 2>&1

echo   Stopping Broker 103...
for /f "tokens=5" %%p in ('netstat -aon ^| findstr :9094 ^| findstr LISTENING') do taskkill /PID %%p /F >nul 2>&1

echo   Stopping Broker 102...
for /f "tokens=5" %%p in ('netstat -aon ^| findstr :9093 ^| findstr LISTENING') do taskkill /PID %%p /F >nul 2>&1

echo   Stopping Broker 101...
for /f "tokens=5" %%p in ('netstat -aon ^| findstr :9092 ^| findstr LISTENING') do taskkill /PID %%p /F >nul 2>&1

echo   Stopping ZooKeeper...
for /f "tokens=5" %%p in ('netstat -aon ^| findstr :2181 ^| findstr LISTENING') do taskkill /PID %%p /F >nul 2>&1

echo.
echo Cluster is DOWN.
