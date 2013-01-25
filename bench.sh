#!/bin/bash

sum="0.0"
for beta in "-72" "-71" "71" "72"
do
    javac src/ISS.java && java -jar resources/ISSVis.jar -exec "java -cp src/ ISS" -model resources/ISS_simple.model -beta $beta > results.dat
    compute="$sum + `grep \"Score\" results.dat | cut -d " " -f 3`"
    sum=`echo "$compute" | bc`
done
echo "$sum / 4" | bc
rm -rf results.dat
