#!/bin/bash
javac -cp lib/external-ui-1.0-SNAPSHOT.jar src/ISS3.java && java -jar resources/ISSVis.jar -exec "java -cp src/:lib/external-ui-1.0-SNAPSHOT.jar ISS3" -model resources/ISS_simple.model -show_power -rendering -beta $1
