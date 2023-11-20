# This scripts allows to switch the real docker-compose.yml file with a
# (manually) instrumented one, for debug purposes only.
# It allows to undo the switch when debug operations are terminated.
# Please provide -debug or -teardown flags when executing.

#!/bin/bash
if [ $# != 1 ]
  then
    echo "Please provide exactly 1 flag:\"-debug\" or \"-teardown\""
    exit 1
fi

if [ $1 = "-debug" ]
    then
        cp -f ../docker-compose.yml ./docker-compose.yml.original
		rm -f ../docker-compose.yml
		cp -f ./docker-compose.yml.debug ../docker-compose.yml
    elif [ $1 = "-teardown" ]
        then
	    rm -f ../docker-compose.yml
	    cp -f ./docker-compose.yml.original ../docker-compose.yml
	    rm -f ./docker-compose.yml.original
    else
    	echo "Invalid flag $1, please provide \"-debug\" or \"-teardown\""
	exit 1
fi
