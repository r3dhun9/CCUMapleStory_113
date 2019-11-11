@echo off
@title Dump
set CLASSPATH=.;lib\*
java -server tools.wztosql.MonsterDropCreator
pause