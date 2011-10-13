#!/bin/bash

cat data/weekly_adherence_logs | while read line
do
    echo $line
    if [[ "$line" = --* ]];
        then continue
    fi
    echo "Creating weekly adherence log.." $line
    ant -f setup.xml setup.weekly.adherence.logs -Dparams="$line"
done