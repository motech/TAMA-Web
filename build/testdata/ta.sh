#!/bin/bash

cat data/treatment_advice | while read line
do
    echo $line
    if [[ "$line" = --* ]];
        then continue
    fi
    echo "Updating treatment advice start date:" $line
    ant -f setup.xml update.treatment.advice -Dparams="$line"
done