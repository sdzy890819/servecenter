#!/bin/bash
SERVER_NAME=$1
JAR_FILE=$2
CUSTOM_OPS=$3
echo "启动 $SERVER_NAME"
PIDS=`jps|awk '/'"$SERVER_NAME"'/ {print $1}'`
echo "PIDS:$PIDS"
if [ -z "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME does not started!"
else
    echo -e "Stopping the $SERVER_NAME ..."
    for PID in $PIDS ; do
       kill $PID || kill -9 $PID
       for((i=0;i<15;i++))
       do
         if [ -n "`jps|awk '/'"$SERVER_NAME"'/ {print $1}'`" ]; then
            echo " waitting server shutdown for 1s ..."
            sleep 1s
         fi
       done
       if [ -n "`jps|awk '/'"$SERVER_NAME"'/ {print $1}'`" ]; then
           echo " kill server failed for 15s ,use kill -9 $PID ... "
           kill -9 $PID
       fi
    done
fi
APP_OPTS=""
if [ ! -z "$SPRING_PROFILE" ]; then
    echo "SPRING_PROFILE: $SPRING_PROFILE"
    APP_OPTS="-Dspring.profiles.active=$SPRING_PROFILE"
fi
if [ ! -z "$SPRING_CONFIG_NAME" ]; then
    echo "$SPRING_CONFIG_NAME: $SPRING_CONFIG_NAME"
    APP_OPTS="-Dspring.config.name=$SPRING_CONFIG_NAME"
fi

if [ ! -z "$JACOCO" ]; then
    echo "JACOCO: $JACOCO"
    JACOCO_OPTS="$JACOCO"
fi

#LOG_DIR----------------------------------------#
cd ..
CURRENT_DIR=`pwd`
echo "CURRENT_DIR: $CURRENT_DIR"
LOGS_DIR="$HOME/logs/${SERVER_NAME}"
if [ ! -d $LOGS_DIR ]; then
    mkdir -p $LOGS_DIR
fi
STDOUT_FILE=$LOGS_DIR/${SERVER_NAME}-stdout.log
GC_LOG=$LOGS_DIR/${SERVER_NAME}-gc.log
DUMP_LOG=$LOGS_DIR/${SERVER_NAME}-dump.log
#JAVA_MEM_OPTS#
JVM_OPTS=""


if [ "test" = "$SYS_ENVIRONMENT" ]; then
    JVM_OPTS=" -server -Xmx586m -Xms586m -Xmn128m -XX:PermSize=64m -XX:MaxPermSize=128m -Xss256k -XX:SurvivorRatio=2 -XX:+UseParallelGC"
elif [ "dev" = "$SYS_ENVIRONMENT" ]; then
    JVM_OPTS=" -server -Xmx586m -Xms586m -Xmn128m -XX:PermSize=64m -XX:MaxPermSize=128m -Xss256k -XX:SurvivorRatio=2 -XX:+UseParallelGC"
elif [ -z "$SYS_ENVIRONMENT" ]||[ "prod" = "$SYS_ENVIRONMENT" ]; then
    JVM_OPTS=" -server -Xmx1024m -Xms1024m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=256m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=75"
fi

GC_OPS=" -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:${GC_LOG} -XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=${DUMP_LOG}"

JVM_OPTS="${JVM_OPTS}${GC_OPS}"

echo -e "Starting the $SERVER_NAME ..."

echo "nohup java $CUSTOM_OPS $JVM_OPTS $APP_OPTS $JACOCO_OPTS -jar $JAR_FILE > $STDOUT_FILE 2>&1 &"
nohup java $CUSTOM_OPS $JVM_OPTS $APP_OPTS $JACOCO_OPTS -jar $JAR_FILE > $STDOUT_FILE 2>&1 &
#java $CUSTOM_OPS $JVM_OPTS $APP_OPTS -jar $JAR_FILE > $STDOUT_FILE 2>&1
sleep 3s
if [ -z "`jps|awk '/'"$SERVER_NAME"'/ {print $1}'`" ]; then
    echo "ERROR: The server $SERVER_NAME start failed ...!"
    exit 1
else
    echo "The server $SERVER_NAME start success ...!"
fi
echo -e \\n