@echo off
echo ===================================================
echo   Compilando o Projeto SisVendas (POO)
echo ===================================================

if not exist bin mkdir bin

echo Compilando fontes Java...
javac -encoding UTF-8 -d bin src\main\java\br\com\sisvendas\model\*.java src\main\java\br\com\sisvendas\dao\*.java src\main\java\br\com\sisvendas\service\*.java src\main\java\br\com\sisvendas\view\*.java src\main\java\br\com\sisvendas\Main.java

if %ERRORLEVEL% equ 0 (
    echo [SUCESSO] Compilacao concluida com sucesso na pasta 'bin'!
) else (
    echo [ERRO] Falha durante a compilacao. Verifique se o JDK esta instalado e configurado no PATH.
)
pause
