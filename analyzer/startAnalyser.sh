#!/bin/bash

#mkdir ./statistic
#mkdir ./statistic/nuovoScheduler
#mkdir ./statistic/nuovoScheduler/ToTSchedule
#mkdir ./statistic/nuovoScheduler/PerSchedule

mkdir ./statistic/nuovoScheduler2
mkdir ./statistic/nuovoScheduler2/ToTSchedule
mkdir ./statistic/nuovoScheduler2/PerSchedule

pip install pandas
pip install numpy
pip install matplotlib
pip install pg8000
pip install requests

#python3 measurerAndAnalyzer.py
#python3 HypothesisTest.py > risultatiDeiTest.txt
python3 Plotter.py