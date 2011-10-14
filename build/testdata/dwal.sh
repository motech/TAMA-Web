#!/bin/bash

echo "Deleting all weekly logs.."
ant -f setup.xml drop.all.weekly.logs.for.patient
