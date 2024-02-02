#!/bin/bash
rm -d -r ./statistic
rm nuovoSchedulertmp.txt
rm vecchioSchedulertmp.txt

mkdir ./statistic
mkdir ./statistic/nuovoScheduler
mkdir ./statistic/vecchioScheduler
python3 measurerAndAnalyzer.py
