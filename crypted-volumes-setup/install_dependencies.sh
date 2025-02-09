#!/bin/bash
set -e

echo "Aggiornamento del sistema e installazione dei pacchetti necessari..."
sudo apt update && sudo apt install -y cryptsetup e2fsprogs

echo "Creazione delle cartelle necessarie..."
sudo mkdir -p /var/lib/docker_volumes
sudo mkdir -p /mnt/db_data

echo "Setup completato! Ora esegui lo script di setup dei volumi."