set "CATALINA_HOME=C:\tools\tomcat\apache-tomcat-7.0.2"
CALL C:\tools\tomcat\apache-tomcat-7.0.2\bin\shutdown.bat
DEL C:\tools\tomcat\apache-tomcat-7.0.2\webapps\IGOR-Web\* /Q
COPY D:\Users\krm\Documents\STUFF\Work\IGOR-Web-Metro\IGOR-Web-Metro\war\* C:\tools\tomcat\apache-tomcat-7.0.2\webapps\IGOR-Web /Y
CALL C:\tools\tomcat\apache-tomcat-7.0.2\bin\startup.bat