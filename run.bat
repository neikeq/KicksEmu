@ECHO off

TITLE KicksEmulator

for /f "tokens=1,* delims= " %%a in ("%*") do set ARGS=%%b

gradlew.bat run -Pargs="%1" ARGS

PAUSE
