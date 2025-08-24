@ECHO OFF
:: Declaration des variables

rem Le chemin du poste de travail
set work_dir=.

rem Le chemin de deployement final
set webapps=C:\Program Files\Apache Software Foundation\Tomcat 11.0\webapps

rem Le dossier de configuration du  meta-inf
set meta=%work_dir%\META-INF

rem Le dossier de configuration du web xml
set web_xml=%work_dir%\web.xml

rem Le dossier temp dans le dossier de travail
set temp=%work_dir%\temp

rem Le dossier de librairies d'independances
set lib=%work_dir%\lib

rem Le dossier des fichiers sources java
set src=%work_dir%\src

rem Le dossier web inf de temp
set web_inf=%temp%\WEB-INF

rem Le dossier views de temp
set view=%temp%\views

rem Le contenue du dossier views 
set test_jsp=%work_dir%\views\*

@rem Tester si le dossier temp existe deja
if exist "%temp%" (
    :: Supprimer le dossier temp si il existe deja
    rmdir "%temp%" /s /q
)
:: Recree le dossier temp
mkdir "%temp%"

@rem Creation de la structure de deployement
:: Creation du dossier views
mkdir "%view%"
:: Creation du dossier WebInf
mkdir "%web_inf%"
:: Creation de web inf lib
set  web_inf_lib=%web_inf%\lib
mkdir "%web_inf_lib%"
:: Creation de web inf classes
set  web_inf_cls=%web_inf%\classes
mkdir "%web_inf_cls%"
:: Creation de meta inf persisatnce
set  web_inf_meta=%web_inf_cls%\META-INF
mkdir "%web_inf_meta%"

@REM Compilation des fichiers source Java
:: Correction -> ne lister que les fichiers .java
dir /s /b "%src%\*.java" > src.txt

for /f "tokens=*" %%a in ('type "src.txt"') do (
    echo [Compilation] "%%a"
    javac -d "%web_inf_cls%" "%%a" -cp "%lib%\*";"%src%"
)
del src.txt > NUL

@rem Copie des fichiers avant deployement
:: Copier le meta-inf
xcopy "%meta%" "%web_inf_meta%" /s /e /h
:: Copier le fichier jsp
xcopy "%test_jsp%" "%temp%\views" /s /e /h
:: Copier le fichier web xml
xcopy "%web_xml%" "%web_inf%"
:: Transfert des librairies
xcopy "%lib%" "%web_inf%\lib" /s /e /h

@REM xcopy "%temp%\views" "C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps\Avion" /s /e /h
@REM xcopy "%temp%\WEB-INF" "C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps\Avion" /s /e /h