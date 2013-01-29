#!/bin/bash

echo "Compiling solution"
javac src/ISS.java

echo "Clearing results"
rm -rf results/*

for beta in "-70" "70" "-75" "75" "-72" "72" "-71" "71" "-74" "74" "-73" "73"
do

for yaw in "0.0" "3.0"
do

for elevation in "15.0" "17.0" "19.0" "21.0" "23.0"
do

for skewFactor in "0.0" "3.0" "6.0"
do

for offsetStart in "355.0" "0.0" "5.0" "10.0"
do
	filename=results/res:$beta:$elevation:$yaw:$skewFactor:$offsetStart
    java -jar resources/ISSVis.jar -exec "java -cp src/ ISS ${elevation} ${yaw} ${skewFactor} ${offsetStart}" -model resources/ISS_simple.model -beta ${beta} > ${filename} &
done
wait

for offsetStart in "355.0" "0.0" "5.0" "10.0"
do
	filename=results/res:$beta:$elevation:$yaw:$skewFactor:$offsetStart
	echo "Processing ${filename} results"
    tail -n 1 ${filename}
    echo ${filename} >> ${filename}
done
done
done
done
done