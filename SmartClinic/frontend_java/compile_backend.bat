@echo off
cd /d "%~dp0"
echo [INFO] Compiling C++ Backend...

:: 1. Create the folder where Java expects the executable (inside frontend_java)
if not exist "backend_cpp" mkdir "backend_cpp"

:: 2. Check for G++ compiler
where g++ >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] G++ compiler not found. Please install MinGW or add G++ to PATH.
    pause
    exit /b
)

:: 3. Compile C++ source from the sibling folder (..\backend_cpp)
echo [INFO] Compiling files from ..\backend_cpp...
g++ "..\backend_cpp\*.cpp" -o "backend_cpp\backend.exe"

if exist "backend_cpp\backend.exe" (
    echo [SUCCESS] backend.exe created successfully!
) else (
    echo [ERROR] Compilation failed.
)

pause