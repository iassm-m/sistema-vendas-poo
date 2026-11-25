@echo off
setlocal
echo ===================================================
echo   Iniciando o SisVendas (POO - UVA)
echo ===================================================

set "JAVA_CMD=java"
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    if exist "%~dp0..\jdk\jdk-17.0.21.1+2\bin\java.exe" (
        set "JAVA_CMD=%~dp0..\jdk\jdk-17.0.21.1+2\bin\java.exe"
    )
)

if not exist bin\br\com\sisvendas\Main.class (
    echo Compilando fontes primeiro...
    call "%~dp0build.bat"
)

echo Usando Java: %JAVA_CMD%
echo Iniciando interface grafica do SisVendas...
start "" "%JAVA_CMD%" -cp "%~dp0bin" br.com.sisvendas.Main
endlocal
