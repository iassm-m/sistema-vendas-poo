@echo off
echo ===================================================
echo   Iniciando o SisVendas (POO)
echo ===================================================

if not exist bin\br\com\sisvendas\Main.class (
    echo Compilando fontes primeiro...
    call build.bat
)

echo Executando a aplicacao...
java -cp bin br.com.sisvendas.Main
pause
