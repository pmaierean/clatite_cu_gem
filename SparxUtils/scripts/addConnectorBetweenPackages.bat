echo off
SET JAVA32PATH=C:\Program Files (x86)\Java\jdk1.8.0_191
SET JAVA32LIB=%JAVA32PATH%\jre\lib
SET JAVA32="%JAVA32PATH%\bin\java.exe" 
SET CRT_DIR=%cd%

SET SPARX_API_PATH=C:\Program Files (x86)\Sparx Systems\EA\Java API

SET CLP="%JAVA32LIB%\resources.jar"
SET CLP=%CLP%;"%JAVA32LIB%\rt.jar"
SET CLP=%CLP%;"%JAVA32LIB%\jsse.jar"
SET CLP=%CLP%;"%JAVA32LIB%\jce.jar"
SET CLP=%CLP%;"%JAVA32LIB%\charsets.jar"
SET CLP=%CLP%;"%JAVA32LIB%\jfr.jar"
SET CLP=%CLP%;"%JAVA32LIB%\ext\access-bridge-32.jar"
SET CLP=%CLP%;"%JAVA32LIB%\ext\cldrdata.jar"
SET CLP=%CLP%;"%JAVA32LIB%\ext\dnsns.jar"
SET CLP=%CLP%;"%JAVA32LIB%\ext\jaccess.jar"
SET CLP=%CLP%;"%JAVA32LIB%\ext\jfxrt.jar"
SET CLP=%CLP%;"%JAVA32LIB%\ext\localedata.jar"
SET CLP=%CLP%;"%JAVA32LIB%\ext\nashorn.jar"
SET CLP=%CLP%;"%JAVA32LIB%\ext\sunec.jar"
SET CLP=%CLP%;"%JAVA32LIB%\ext\sunjce_provider.jar"
SET CLP=%CLP%;"%JAVA32LIB%\ext\sunmscapi.jar"
SET CLP=%CLP%;"%JAVA32LIB%\ext\sunpkcs11.jar"
SET CLP=%CLP%;"%JAVA32LIB%\ext\zipfs.jar"
SET CLP=%CLP%;"%CRT_DIR%\..\target\SparxUtils-0.0.1-SNAPSHOT.jar"
SET CLP=%CLP%;"%SPARX_API_PATH%\eaapi.jar"

echo on

%JAVA32% -Djava.library.path="%SPARX_API_PATH%" -Dfile.encoding=Cp1252 -classpath %CLP% com.maiereni.sample.sparx.AddConnectorBetweenPackages %1 %2 %3 %4