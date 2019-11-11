#!/bin/bash

export CLASSPATH=.:dist/*:lib/*

java -Xmx1500m -Dfile.encoding=UTF-8 server.Start
