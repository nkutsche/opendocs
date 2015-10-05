
set VERSION=%1
set TARGET=%2
set BASE=%~dp0
set SAXON=%BASE%..\lib\saxon8.jar
set ADDON_FILE=oxygen

java -jar %SAXON% -o %BASE%..\releases\%VERSION%\%ADDON_FILE%.xml %BASE%pluginTemplate.xml %BASE%createPlugin.xsl version=%VERSION% target=%TARGET% release=%BASE%..\releases\%ADDON_FILE%
