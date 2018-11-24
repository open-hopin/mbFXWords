#!/bin/sh
set -x
mkdir dist/resources/
cp resources/* dist/resources/
#echo -n "prompt"  #'-n' means do not add \n to end of string
#read              # No arg means dump next line of input
#read -p "Press [Enter] key to start backup..."
read -p 'Press Enter to continue...' var
