#!/bin/bash

cat data/patient | while read line
do
    echo $line
    if [[ "$line" = --* ]];
        then continue
    fi
    echo "Creating patient:" $line
    ant -f setup.xml setup.patient -Dparams="$line"
done