#!/bin/bash

mkdir ./statistic
mkdir ./statistic/vecchioScheduler
mkdir ./statistic/vecchioScheduler/PerSchedule
mkdir ./statistic/vecchioScheduler/ToTSchedule
python3 measurerAndAnalyzer.py
