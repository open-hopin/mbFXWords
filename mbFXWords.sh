#!/bin/sh -x
#echo $0
#BASEDIR=$(dirname $0)
#echo "Script location: ${BASEDIR}"
DIR="$( cd "$( dirname "$0" )" && pwd )"
#echo $DIR
java -splash:$DIR/resources/FlatXoreo.png -jar $DIR/mbFXWords.jar "$1" $2
read -p 'Press Enter to close shell...' var
