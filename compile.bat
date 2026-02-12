@echo off
echo Compiling Assessment Feedback System...

if not exist "bin" mkdir bin

javac -d bin src\utils\*.java src\common\*.java src\admin\*.java src\leader\*.java src\lecturer\*.java src\student\*.java src\Main.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilation successful!
    echo Run 'run.bat' to start the application.
) else (
    echo.
    echo Compilation failed! Please check for errors.
)

pause
