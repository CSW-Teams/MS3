#!/bin/bash
set -e
# password: 1234
CRYPTSETUP_PATH="/usr/sbin/cryptsetup"  # Cambia questo se il percorso è diverso
VOLUME_PATH="/var/lib/docker/volumes/ms3_db_data.img"
MOUNT_POINT="/mnt/ms3_db_data"
LUKS_NAME="ms3_db_data_crypt"
VOLUME_SIZE="5G"

if [ ! -f "$VOLUME_PATH" ]; then
    echo "Creazione del file immagine per il volume cifrato..."
    sudo dd if=/dev/urandom of=$VOLUME_PATH bs=1M count=5000 status=progress

    echo "Inizializzazione di LUKS..."
    echo -n "Inserisci una password per cifrare il volume: "
    sudo $CRYPTSETUP_PATH luksFormat $VOLUME_PATH

    echo "Aprire il volume per formattarlo..."
    sudo $CRYPTSETUP_PATH open $VOLUME_PATH $LUKS_NAME
    sudo mkfs.ext4 /dev/mapper/$LUKS_NAME
    sudo $CRYPTSETUP_PATH close $LUKS_NAME
    echo "Volume cifrato configurato correttamente!"
else
    echo "Il file di volume cifrato esiste già, nessuna azione necessaria."
fi
