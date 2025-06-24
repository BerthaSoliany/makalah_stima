@echo off
echo Running Branching Narrative Optimization System...
echo.

if not exist "bin\com\story\StoryApplication.class" (
    echo Class files not found. Please compile first using build.bat
    pause
    exit /b 1
)

java -cp bin com.story.StoryApplication
pause
