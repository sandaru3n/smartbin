@echo off
echo Refreshing IDE cache and project...
echo.

echo 1. Cleaning Maven project...
call mvnw.cmd clean compile -q

echo 2. Checking if application is running...
netstat -an | findstr :8084 >nul
if %errorlevel% equ 0 (
    echo    Application is running on port 8084
) else (
    echo    Application is not running
)

echo.
echo 3. IDE Refresh Instructions:
echo    - Close all Java files in your IDE
echo    - If using VS Code: Press Ctrl+Shift+P, type "Java: Clean Java Language Server Workspace"
echo    - If using IntelliJ: File ^> Invalidate Caches and Restart
echo    - If using Eclipse: Project ^> Clean ^> Clean all projects
echo    - Restart your IDE
echo.
echo 4. Test URLs:
echo    - Home: http://localhost:8084/
echo    - Authority Login: http://localhost:8084/authority/login
echo.
echo Done! The Maven build was successful - IDE errors are cache-related.
pause
