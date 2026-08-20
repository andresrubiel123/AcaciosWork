@echo off
title Instalador AcaciosWork Android
color 0B

set SDK_PATH=%LOCALAPPDATA%\Android\Sdk
set EMULATOR_EXE=%SDK_PATH%\emulator\emulator.exe
set ADB_EXE=%SDK_PATH%\platform-tools\adb.exe
set ANDROID_DIR=%~dp0acacioswork-android

:menu
cls
echo =========================================================
echo       ACACIOSWORK - INSTALADOR DE APLICACION ANDROID
echo =========================================================
echo.
echo [1] Iniciar Emulador (Pixel_7) e Instalar Aplicacion
echo [2] Instalar directamente en dispositivo/emulador (USB)
echo [3] Compilar APK y copiar a la raiz (Instalacion manual)
echo [4] Limpiar compilacion y reinstalar (Limpieza Gradle)
echo [5] Salir
echo.
echo =========================================================
set /p opt="Seleccione una opcion [1-5]: "

if "%opt%"=="1" goto option1
if "%opt%"=="2" goto option2
if "%opt%"=="3" goto option3
if "%opt%"=="4" goto option4
if "%opt%"=="5" goto exit
goto menu

:option1
echo.
echo [1/3] Iniciando emulador Pixel_7 en segundo plano...
if not exist "%EMULATOR_EXE%" (
    color 0C
    echo Error: No se encontro el emulador en la ruta: %EMULATOR_EXE%
    pause
    goto menu
)
start "" "%EMULATOR_EXE%" -avd Pixel_7
echo [2/3] Esperando a que el emulador este en linea (wait-for-device)...
"%ADB_EXE%" wait-for-device
echo Emulador detectado.
goto install_process

:option2
echo.
echo [1/2] Verificando dispositivos conectados...
"%ADB_EXE%" devices
echo.
echo [2/2] Esperando conexion de dispositivo...
"%ADB_EXE%" wait-for-device
goto install_process

:option3
echo.
echo [1/2] Compilando APK con Gradle...
cd /d "%ANDROID_DIR%"
call .\gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    color 0C
    echo.
    echo [ERROR] La compilacion fallo.
    cd /d "%~dp0"
    pause
    color 0B
    goto menu
)
echo.
echo [2/2] Copiando APK a la raiz del proyecto...
copy /y "%ANDROID_DIR%\app\build\outputs\apk\debug\app-debug.apk" "%~dp0AcaciosWork.apk" > nul
color 0A
echo.
echo [EXITO] APK compilado y copiado a la raiz como "AcaciosWork.apk"
cd /d "%~dp0"
pause
color 0B
goto menu

:option4
echo.
echo [1/3] Limpiando compilacion previa del proyecto Android...
cd /d "%ANDROID_DIR%"
call .\gradlew.bat clean
cd /d "%~dp0"
echo.
echo [2/3] Esperando conexion de dispositivo...
"%ADB_EXE%" wait-for-device
goto install_process

:install_process
echo.
echo [Procesando] Iniciando compilacion e instalacion mediante Gradle...
cd /d "%ANDROID_DIR%"
call .\gradlew.bat installDebug
if %errorlevel% neq 0 (
    echo [Info] Copiando APK pre-compilado a la raiz como soporte...
    copy /y "%ANDROID_DIR%\app\build\outputs\apk\debug\app-debug.apk" "%~dp0AcaciosWork.apk" > nul
    color 0C
    echo.
    echo [ERROR] La instalacion fallo. Se copio el APK a la raiz como "AcaciosWork.apk" para instalacion manual.
    cd /d "%~dp0"
    pause
    color 0B
    goto menu
)
echo.
echo Copiando APK exitoso a la raiz...
copy /y "%ANDROID_DIR%\app\build\outputs\apk\debug\app-debug.apk" "%~dp0AcaciosWork.apk" > nul
color 0A
echo.
echo [EXITO] Aplicacion instalada correctamente en el dispositivo.
echo [Info] Iniciando aplicacion en el dispositivo...
"%ADB_EXE%" shell monkey -p com.acacioswork -c android.intent.category.LAUNCHER 1
cd /d "%~dp0"
pause
color 0B
goto menu

:exit
cls
echo Gracias por usar el instalador de AcaciosWork.
timeout /t 2 > nul
exit
