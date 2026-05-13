@echo off
REM
REM Clean Kafka data (ZooKeeper + broker logs)
REM Run this if brokers show "Node disconnected" in Kafka UI
REM Then restart with start-kafka.bat
REM

echo Cleaning Kafka data...

echo   Cleaning ZooKeeper data...
rmdir /s /q "C:\tmp\zookeeper" 2>nul

echo   Cleaning broker logs...
rmdir /s /q "C:\tmp\kafka-logs-101" 2>nul
rmdir /s /q "C:\tmp\kafka-logs-102" 2>nul
rmdir /s /q "C:\tmp\kafka-logs-103" 2>nul

echo.
echo Done! Now run: start-kafka.bat
