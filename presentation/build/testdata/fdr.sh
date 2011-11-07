#!/bin/bash

if [ -z "$1" ]
then
   echo "Usage: ./fdr.sh <patient_id>"
   exit 3
fi

echo "Triggering event:" $1
ant -f setup.xml trigger.four.day.recall.event -Dparams="$1"
