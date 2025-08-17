@echo off
echo ========================================
echo    BUILD COMPLET - COMPILATION + WAR
echo ========================================

set APP_NAME=Avion

REM ========================================
REM ÉTAPE 1: COMPILATION JAVA
REM ========================================

echo Étape 1: Compilation des fichiers Java...

REM Définir le classpath avec toutes les dépendances
set CLASSPATH=lib\Meframework.jar;lib\postgresql-42.7.2.jar;lib\hibernate-core-6.4.4.Final.jar;lib\hibernate-entitymanager-5.6.15.Final.jar;lib\jakarta.persistence-api-3.1.0.jar;lib\jakarta.servlet-api-6.0.0.jar;lib\gson-2.10.1.jar;lib\*

REM Créer le dossier build/classes s'il n'existe pas
if not exist "build\classes" mkdir build\classes

REM Compiler tous les fichiers Java
javac -cp "%CLASSPATH%" -d build\classes src\main\java\controller\*.java src\main\java\model\*.java src\main\java\service\*.java src\main\java\util\*.java

if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Échec de la compilation Java!
    echo Vérifiez vos fichiers Java et les dépendances dans lib/
    pause
    exit /b %ERRORLEVEL%
)

echo ✓ Compilation Java réussie!

REM ========================================
REM ÉTAPE 2: COPIER LES RESSOURCES
REM ========================================

echo Étape 2: Copie des ressources...

REM Créer le dossier build/webapp s'il n'existe pas
if not exist "build\webapp" mkdir build\webapp

REM Copier les ressources et les fichiers webapp
if exist "src\main\resources" xcopy /s /y /q "src\main\resources\*" "build\classes\"
if exist "src\main\webapp" xcopy /s /y /q "src\main\webapp\*" "build\webapp\"

echo ✓ Ressources copiées!

REM ========================================
REM ÉTAPE 3: VÉRIFIER LES FICHIERS COMPILÉS
REM ========================================

echo Étape 3: Vérification des fichiers compilés...

if not exist "build\classes\controller\LoginController.class" (
    echo ERREUR: LoginController.class non trouvé!
    echo La compilation a échoué ou les packages sont incorrects.
    pause
    exit /b 1
)

echo ✓ Fichiers .class vérifiés!

REM ========================================
REM ÉTAPE 4: CRÉATION DU WAR
REM ========================================

echo Étape 4: Création du fichier WAR...

REM Nettoyer le dossier temporaire précédent
if exist "deploy-temp" rmdir /s /q "deploy-temp"

REM Créer structure temporaire WAR
mkdir "deploy-temp"

REM Copier la structure web (JSP, CSS, etc.)
if exist "build\webapp" xcopy /s /y /q "build\webapp\*" "deploy-temp\"

REM Créer WEB-INF/classes s'il n'existe pas
if not exist "deploy-temp\WEB-INF\classes" mkdir "deploy-temp\WEB-INF\classes"

REM Copier TOUS les fichiers compilés (.class)
xcopy /s /y /q "build\classes\*" "deploy-temp\WEB-INF\classes\"

REM Créer WEB-INF/lib s'il n'existe pas
if not exist "deploy-temp\WEB-INF\lib" mkdir "deploy-temp\WEB-INF\lib"

REM Copier toutes les dépendances JAR
copy /y "lib\*.jar" "deploy-temp\WEB-INF\lib\"

REM ========================================
REM ÉTAPE 5: CRÉER LE FICHIER WAR
REM ========================================

echo Étape 5: Génération du fichier WAR...

cd deploy-temp
jar -cvf "..\%APP_NAME%.war" *
cd ..

if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Échec de la création du WAR!
    pause
    exit /b %ERRORLEVEL%
)

REM ========================================
REM ÉTAPE 6: NETTOYAGE ET VÉRIFICATION
REM ========================================

echo Étape 6: Nettoyage...

REM Supprimer le dossier temporaire
rmdir /s /q "deploy-temp"

REM Vérifier que le WAR existe et n'est pas vide
if not exist "%APP_NAME%.war" (
    echo ERREUR: Le fichier WAR n'a pas été créé!
    pause
    exit /b 1
)

REM Vérifier la taille du WAR
for %%A in ("%APP_NAME%.war") do set WAR_SIZE=%%~zA
if %WAR_SIZE% LSS 1000 (
    echo ATTENTION: Le fichier WAR semble très petit (%WAR_SIZE% bytes)
    echo Il pourrait y avoir un problème.
)

echo ========================================
echo           BUILD TERMINÉ !
echo ========================================
echo Fichier créé: %APP_NAME%.war
echo Taille: %WAR_SIZE% bytes
echo.
