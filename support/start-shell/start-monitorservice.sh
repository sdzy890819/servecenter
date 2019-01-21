#!/bin/bash
SERVER_NAME="monitorservice"
JAR_FILE="/data/app/$SERVER_NAME-boot.jar"
CUSTOMER=" "
./start.sh $SERVER_NAME $JAR_FILE $CUSTOMER