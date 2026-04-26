@echo off
echo ====================================
echo   🌿 Vitaal - Backend Nutricional
echo ====================================
echo.

echo 1. Instalando dependencias...
pip install -r requirements.txt

echo.
echo 2. Iniciando servidor...
echo.
echo    Acesse: http://localhost:5000
echo    Para PWA: Use o endereco do seu servidor
echo.
python app.py