#!/bin/bash

./gradlew run -Pargs="$1" ${@:2}

read -n1 -r -p "Press any key to continue..." key
