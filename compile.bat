@echo off
echo Création du WAR...

set APP_NAME=Avion

REM Créer structure temporaire
mkdir deploy-temp
xcopy /s /y "build\webapp\*" "deploy-temp\"

REM Copier classes compilées
mkdir "deploy-temp\WEB-INF\classes"
xcopy /s /y "build\classes\*" "deploy-temp\WEB-INF\classes\"

REM Copier dépendances
mkdir "deploy-temp\WEB-INF\lib"
copy "lib\*.jar" "deploy-temp\WEB-INF\lib\"

REM Créer le WAR
cd deploy-temp
jar -cvf ..\%APP_NAME%.war *
cd ..

rmdir /s /q deploy-temp
echo WAR créé : %APP_NAME%.war