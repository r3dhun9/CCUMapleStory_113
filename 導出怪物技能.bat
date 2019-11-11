@echo off
@title Dump
set CLASSPATH=.;lib\*
java -server tools.wztosql.DumpMobSkills
pause