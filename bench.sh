#!/bin/bash

javac src/ISS.java
rm -rf results:*.dat

for beta in "70" "71" "72" "73" "74" "75" "-70" "-71" "-72" "-73" "-74" "-75"
do
    java -jar resources/ISSVis.jar -exec "java -cp src/ ISS" -model resources/ISS_simple.model -beta $beta > results:${beta}.dat &
done
wait

sum="0.0"
for beta in "70" "71" "72" "73" "74" "75" "-70" "-71" "-72" "-73" "-74" "-75"
do
	compute="$sum + `grep \"Score\" results:${beta}.dat | cut -d " " -f 3`"
    sum=`echo "$compute" | bc`
done

echo "$sum / 12" | bc