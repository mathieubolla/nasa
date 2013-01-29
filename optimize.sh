#!/bin/bash

echo "Compiling solution"
javac src/ISS.java

echo "Clearing results"
rm -rf results/*

for beta in "-70" "70" "-75" "75" "-72" "72" "71" "73" "74" "-71" "-73" "-74"
do

for yaw in "0.0" "3.5" "7.0"
do

for elevation in "12.0" "15.0" "17.0" "19.0" "22.0"
do

for skewFactor in "0.0" "6.0" "12.0"
do

for offsetStart in "355.0" "0.0" "5.0" "10.0"
do
	filename=results/res:$beta:$elevation:$yaw:$skewFactor:$offsetStart
	echo "Processing ${filename}"
    java -jar resources/ISSVis.jar -exec "java -cp src/ ISS ${elevation} ${yaw} ${skewFactor} ${offsetStart}" -model resources/ISS_simple.model -beta ${beta} > ${filename}
    tail -n 1 ${filename}
    echo ${filename} >> ${filename}
done
done
done
done
done