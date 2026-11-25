@echo off
setlocal
echo ===================================================
echo   Compilando o Projeto SisVendas (POO)
echo ===================================================

set "JAVAC_CMD=javac"
where javac >nul 2>nul
if %ERRORLEVEL% neq 0 (
    if exist "%~dp0..\jdk\jdk-17.0.21.1+2\bin\javac.exe" (
        set "JAVAC_CMD=%~dp0..\jdk\jdk-17.0.21.1+2\bin\javac.exe"
    )
)

if not exist bin mkdir bin

echo Usando compilador: %JAVAC_CMD%
echo Compilando fontes Java...

"%JAVAC_CMD%" -encoding UTF-8 -d bin src\main\java\br\com\sisvendas\model\*.java src\main\java\br\com\sisvendas\dao\*.java src\main\java\br\com\sisvendas\service\*.java src\main\java\br\com\sisvendas\view\*.java src\main\java\br\com\sisvendas\Main.java

if %ERRORLEVEL% equ 0 (
    echo [SUCESSO] Compilacao concluida com sucesso na pasta 'bin'!
) else (
    echo [ERRO] Falha durante a compilacao.
)
endlocal
