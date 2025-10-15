@echo off
echo ========================================
echo Starting SmartBin Application
echo ========================================

REM Set JAVA_HOME (adjust path if your Java is installed elsewhere)
set JAVA_HOME=C:\Program Files\Java\jdk-24

REM Alternative Java paths (uncomment the one that matches your system)
REM set JAVA_HOME=C:\Program Files\Java\jdk-21
REM set JAVA_HOME=C:\Program Files\Java\jdk-17
REM set JAVA_HOME=C:\Program Files\OpenJDK\jdk-21

echo Using Java from: %JAVA_HOME%
echo.

REM Check if Java exists
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: Java not found at %JAVA_HOME%
    echo Please update JAVA_HOME in this script to match your Java installation
    echo.
    echo To find your Java installation, run: where java
    pause
    exit /b 1
)

echo Java version:
"%JAVA_HOME%\bin\java.exe" -version
echo.

echo Starting application...
echo Access dashboard at: http://localhost:8084/authority/dashboard
echo Login: waste@gmail.com / password123
echo.
echo Press Ctrl+C to stop the application
echo ========================================
echo.

REM Run Maven
call mvnw.cmd clean spring-boot:run

pause

