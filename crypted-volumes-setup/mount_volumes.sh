#!/bin/bash
set -e

CRYPTSETUP_PATH="/usr/sbin/cryptsetup"  # Cambia questo se il percorso è diverso
VOLUME_PATH="/var/lib/docker/volumes/ms3_db_data.img"
MOUNT_POINT="/mnt/ms3_db_data"
LUKS_NAME="ms3_db_data_crypt"

if [ ! -f "$VOLUME_PATH" ]; then
    echo "Errore: il file del volume cifrato non esiste. Esegui setup_volumes.sh prima."
    exit 1
fi

echo "Sblocco del volume cifrato..."
echo -n "Inserisci la password per sbloccare il volume: "
sudo $CRYPTSETUP_PATH open $VOLUME_PATH $LUKS_NAME

echo "Montaggio del volume..."
sudo mount /dev/mapper/$LUKS_NAME $MOUNT_POINT

echo "Volume cifrato montato con successo in $MOUNT_POINT!"
