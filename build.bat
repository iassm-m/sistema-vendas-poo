@echo off
setlocal
cd /d "%~dp0"
echo ===================================================
echo   Compilando o Projeto SisVendas (POO)
echo ===================================================

set "JAVAC_CMD="
where javac >nul 2>nul
if %ERRORLEVEL% equ 0 set "JAVAC_CMD=javac"

if not defined JAVAC_CMD if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
if not defined JAVAC_CMD if exist "C:\Program Files\Apache NetBeans\jdk\bin\javac.exe" set "JAVAC_CMD=C:\Program Files\Apache NetBeans\jdk\bin\javac.exe"
if not defined JAVAC_CMD if exist "%~dp0..\jdk\jdk-17.0.21.1+2\bin\javac.exe" set "JAVAC_CMD=%~dp0..\jdk\jdk-17.0.21.1+2\bin\javac.exe"

if not defined JAVAC_CMD (
    echo [ERRO] JDK nao encontrado no PATH, JAVA_HOME ou Apache NetBeans.
    exit /b 1
)

if not exist bin mkdir bin

echo Usando compilador: %JAVAC_CMD%
echo Compilando fontes Java...

"%JAVAC_CMD%" -encoding UTF-8 -d bin src\main\java\br\com\sisvendas\model\*.java src\main\java\br\com\sisvendas\dao\*.java src\main\java\br\com\sisvendas\service\*.java src\main\java\br\com\sisvendas\view\*.java src\main\java\br\com\sisvendas\util\*.java src\main\java\br\com\sisvendas\Main.java

if %ERRORLEVEL% equ 0 (
    echo [SUCESSO] Compilacao concluida com sucesso na pasta 'bin'!
) else (
    echo [ERRO] Falha durante a compilacao.
    exit /b 1
)
endlocal
