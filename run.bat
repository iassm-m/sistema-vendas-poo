@echo off
setlocal
cd /d "%~dp0"
echo ===================================================
echo   Iniciando o SisVendas (POO - UVA)
echo ===================================================

set "JAVA_CMD="
where java >nul 2>nul
if %ERRORLEVEL% equ 0 set "JAVA_CMD=java"

if not defined JAVA_CMD if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
if not defined JAVA_CMD if exist "C:\Program Files\Apache NetBeans\jdk\bin\java.exe" set "JAVA_CMD=C:\Program Files\Apache NetBeans\jdk\bin\java.exe"
if not defined JAVA_CMD if exist "%~dp0..\jdk\jdk-17.0.21.1+2\bin\java.exe" set "JAVA_CMD=%~dp0..\jdk\jdk-17.0.21.1+2\bin\java.exe"

if not defined JAVA_CMD (
    echo [ERRO] Java nao encontrado no PATH, JAVA_HOME ou Apache NetBeans.
    exit /b 1
)

if not exist bin\br\com\sisvendas\Main.class (
    echo Compilando fontes primeiro...
    call "%~dp0build.bat"
    if %ERRORLEVEL% neq 0 exit /b 1
)

echo Usando Java: %JAVA_CMD%
echo Iniciando interface grafica do SisVendas...
start "" /D "%~dp0" "%JAVA_CMD%" -cp "%~dp0bin" br.com.sisvendas.Main
endlocal
