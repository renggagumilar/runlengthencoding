@echo off
REM Change this Script, to suit your needs

REM runs Java2HTML
set MBACKUP=%CLASSPATH%
set CLASSPATH=C:\Incoming\Java\Java2HTML\j2h.jar;%CLASSPATH%
java j2h -js C:\Documents\Java\BIRLE -jd C:\Documents\Java\BIRLE\javadoc
set CLASSPATH=%MBACKUP%
