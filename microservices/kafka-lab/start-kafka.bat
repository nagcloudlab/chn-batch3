@echo off
REM
REM Start Kafka cluster: ZooKeeper + 3 Brokers + Kafka UI
REM Run from the kafka-lab folder: start-kafka.bat
REM

setlocal
cd /d "%~dp0"

echo ================================================
echo   Starting Kafka Cluster
echo ================================================

echo.
echo [1/5] Starting ZooKeeper on port 2181...
start "ZooKeeper" /min cmd /c "cd /d %cd%\kafka-101 && bin\windows\zookeeper-server-start.bat config\zookeeper.properties"
timeout /t 8 /nobreak >nul

echo [2/5] Starting Broker 101 on port 9092...
start "Kafka-101" /min cmd /c "cd /d %cd%\kafka-101 && bin\windows\kafka-server-start.bat config\server.properties"
timeout /t 5 /nobreak >nul

echo [3/5] Starting Broker 102 on port 9093...
start "Kafka-102" /min cmd /c "cd /d %cd%\kafka-102 && bin\windows\kafka-server-start.bat config\server.properties"
timeout /t 5 /nobreak >nul

echo [4/5] Starting Broker 103 on port 9094...
start "Kafka-103" /min cmd /c "cd /d %cd%\kafka-103 && bin\windows\kafka-server-start.bat config\server.properties"
timeout /t 5 /nobreak >nul

echo [5/5] Starting Kafka UI on port 8080...
start "Kafka-UI" /min cmd /c "cd /d %cd%\kafka-ui && java -Dspring.config.additional-location=application.yml -jar kafka-ui-api-v0.7.2.jar"
timeout /t 3 /nobreak >nul

echo.
echo ================================================
echo   Cluster is UP!
echo ================================================
echo   ZooKeeper  : localhost:2181
echo   Broker 101 : localhost:9092
echo   Broker 102 : localhost:9093
echo   Broker 103 : localhost:9094
echo   Kafka UI   : http://localhost:8080
echo ================================================
echo.
echo   To stop: run stop-kafka.bat

endlocal
