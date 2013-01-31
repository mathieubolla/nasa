#!/bin/bash
javac src/ISS.java && java -jar resources/ISSVis.jar -exec "java -cp src/ ISS" -model resources/ISS_simple.model -show_power -beta $1