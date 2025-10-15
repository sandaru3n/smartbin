# SmartBin Application Launcher
# PowerShell Script

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Starting SmartBin Application" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Set JAVA_HOME (adjust if your Java is installed elsewhere)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"

# Alternative paths (uncomment the one that matches your system)
# $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
# $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
# $env:JAVA_HOME = "C:\Program Files\OpenJDK\jdk-21"

Write-Host "Using Java from: $env:JAVA_HOME" -ForegroundColor Yellow
Write-Host ""

# Check if Java exists
$javaExe = Join-Path $env:JAVA_HOME "bin\java.exe"
if (-not (Test-Path $javaExe)) {
    Write-Host "ERROR: Java not found at $env:JAVA_HOME" -ForegroundColor Red
    Write-Host "Please update JAVA_HOME in this script to match your Java installation" -ForegroundColor Red
    Write-Host ""
    Write-Host "To find your Java installation, run: where.exe java" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Display Java version
Write-Host "Java version:" -ForegroundColor Green
& $javaExe -version
Write-Host ""

Write-Host "Starting application..." -ForegroundColor Green
Write-Host "Access dashboard at: http://localhost:8084/authority/dashboard" -ForegroundColor Cyan
Write-Host "Login: waste@gmail.com / password123" -ForegroundColor Cyan
Write-Host ""
Write-Host "Press Ctrl+C to stop the application" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Run Maven
& .\mvnw.cmd clean spring-boot:run

Read-Host "Press Enter to exit"

