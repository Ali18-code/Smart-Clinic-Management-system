@echo off
cd /d "%~dp0"

:: Compile backend first (skips pause if called here, but for now we just run it)
if not exist "backend_cpp\backend.exe" (
    call compile_backend.bat
)

echo [INFO] Compiling and Running Java Frontend...
cd src
javac app/Main.java app/panels/*.java app/util/*.java
java app.Main

pause