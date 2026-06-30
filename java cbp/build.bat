@echo off
echo Compiling Java files...
javac -cp "lib\mysql-connector-j-9.5.0.jar" -d . src\*.java
if %errorlevel% equ 0 (
    echo Compilation successful!
) else (
    echo Compilation failed!
    exit /b 1
)

