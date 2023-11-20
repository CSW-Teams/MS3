#!/bin/bash

cd ../frontend
npm install --legacy-peer-deps
npm audit fix --force
npm start
