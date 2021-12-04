#!/bin/sh

mvn compile exec:java@stats-overview -Dexec.args="output/ls $@"
