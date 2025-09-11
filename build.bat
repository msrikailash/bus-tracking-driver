@echo off
echo ========================================
echo Bus Tracker Manager - Build Script
echo ========================================

REM Check if JAVA_HOME is set
if "%JAVA_HOME%"=="" (
    echo ERROR: JAVA_HOME is not set!
    echo.
    echo Please set JAVA_HOME to your Java installation directory.
    echo Example: set JAVA_HOME=C:\Program Files\Java\jdk-11.0.x
    echo.
    echo Or run this script as Administrator to auto-detect Java.
    pause
    exit /b 1
)

echo JAVA_HOME: %JAVA_HOME%
echo.

REM Clean and build
echo Cleaning project...
call gradlew clean

echo.
echo Building debug APK...
call gradlew assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo BUILD SUCCESSFUL!
    echo ========================================
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
) else (
    echo.
    echo ========================================
    echo BUILD FAILED!
    echo ========================================
    echo Check the error messages above.
    echo.
)

pause







