#!/bin/bash

cat data/clinician | while read line
do
    echo $line
    if [[ "$line" = --* ]];
        then continue
    fi
    echo "Creating clinician:" $line
    ant -f setup.xml setup.clinician -Dparams="$line"
done