@echo off
setlocal EnableExtensions

chcp 65001 >nul

set "ROOT=%~dp0"
set "SRC=%ROOT%src"
set "TOOLS=C:\Compiladores\tools"

if "%1"=="" (
  echo ERROR: No se proporciono archivo
  exit /b 1
)

set "INPUT=%~1"

set "JFLEX_JAR="
for %%F in ("%TOOLS%\jflex*-full*.jar" "%TOOLS%\jflex*.jar") do (
  if exist "%%~fF" set "JFLEX_JAR=%%~fF"
)

set "CUP_JAR="
for %%F in ("%TOOLS%\java-cup*.jar" "%TOOLS%\cup*.jar") do (
  if exist "%%~fF" set "CUP_JAR=%%~fF"
)

if "%JFLEX_JAR%"=="" (
  echo ERROR: No se encontro JFlex
  exit /b 1
)

if "%CUP_JAR%"=="" (
  echo ERROR: No se encontro CUP
  exit /b 1
)

pushd "%SRC%"

java -jar "%JFLEX_JAR%" Lexer.flex >nul
if errorlevel 1 (
  echo ERROR: Fallo JFlex
  popd
  exit /b 1
)

java -jar "%CUP_JAR%" -parser parser -symbols sym parser.cup >nul
if errorlevel 1 (
  echo ERROR: Fallo CUP
  popd
  exit /b 1
)

javac -cp ".;%CUP_JAR%" *.java >nul
if errorlevel 1 (
  echo ERROR: Fallo compilacion
  popd
  exit /b 1
)

if exist "%INPUT%" (
  set "TARGET=%INPUT%"
) else (
  set "TARGET=%ROOT%%INPUT%"
)

java -Dfile.encoding=UTF-8 -cp ".;%CUP_JAR%" Main "%TARGET%"

popd
endlocal
