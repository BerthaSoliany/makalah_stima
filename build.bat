@echo off
echo Compiling program...

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile all Java files
javac -d bin -cp src src/main/java/com/story/*.java src/main/java/com/story/model/*.java src/main/java/com/story/algorithm/*.java src/main/java/com/story/simulator/*.java src/main/java/com/story/util/*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo To run the application, execute:
    echo java -cp bin com.story.StoryApplication or .\run.bat
    echo.
) else (
    echo Compilation failed!
    pause
)
