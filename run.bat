@echo off
title Library Management System
echo Launching Library Management System...
java -jar target/library-management-system-1.0-SNAPSHOT.jar
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Launch failed. Make sure Java 11 or higher is installed.
    pause
)
