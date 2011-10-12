#!/bin/bash

cat data/clinic | while read line
do
    if [[ "$line" = --* ]];
        then continue
    fi
    echo "Creating clinic:" $line
    ant -f setup.xml setup.clinic -Dparams="$line"
done