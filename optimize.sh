#!/bin/bash

elevation=17.0
skewFactor=6.0
offsetStart=0.0

for beta in "70" "71" "72" "73" "74" "75" "-70" "-71" "-72" "-73" "-74" "-75"
do

for yaw in "0.0" "3.0" "5.0" "7.0"
do
    javac src/ISS.java && java -jar resources/ISSVis.jar -exec "java -cp src/ ISS 17.0 ${yaw} 6.0 0.0" -model resources/ISS_simple.model -beta ${beta} > results/res:$beta:$elevation:$yaw:$skewFactor:$offsetStart:results:visu
    grep \"Score\" results/res:$beta:$elevation:$yaw:$skewFactor:$offsetStart:results:visu | cut -d " " -f 3 > results/res:$beta:$elevation:$yaw:$skewFactor:$offsetStart:results:final
done
done
