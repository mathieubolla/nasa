#!/bin/bash

rm -f /tmp/scores.tmp

for file in ./results/*
do
	score=Failure
	scoreline=`tail -n 2 $file | head -n 1 | grep Score`
	status=$?

	if [ $status -eq 0 ]
	then
		score=`echo $scoreline | cut -d " " -f 3`
	fi
	echo "${score} ${file}" >> /tmp/scores.tmp
done

cat /tmp/scores.tmp | sort -nr