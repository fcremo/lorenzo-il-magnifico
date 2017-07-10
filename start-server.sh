#!/bin/bash

pushd `dirname $0` > /dev/null
SCRIPT_PATH=`pwd`
popd > /dev/null

exec java -jar $SCRIPT_PATH/target/client.jar