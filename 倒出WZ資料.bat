@echo off
@title WZStringDumper
set CLASSPATH=.;dist\*
java -server tools.wztosql.WzStringDumper
pause