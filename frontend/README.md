# 🖥️ MS3 Frontend

## ⚠️ Nota bene 1
Se alla fine di questa guida il progetto viene lanciato correttemente ma non riesce a contattare il backend tramite API rest, sarebbe opportuno scaricare e attivare l'estensione per browser [ _Allow CORS: Access-Control-Allow-Origin_](https://chromewebstore.google.com/detail/allow-cors-access-control/lhobafahddgcelffkeicbaginigeejlf).

## 🤚 Nota bene 2 (Opzionale)
Dare i permessi da superuser ad npm è pericoloso per via del
grande numero di dipendenze esterne che esso si porta dietro ogni volta, si consiglia
quindi di creare un ambiente isolato (es. con conda) per evitare problemi di sicurezza
interni alla proprio macchina ed usare npm al suo interno.

## 🛠️ Installazione
Prima di tutto, installare [Node.js](https://nodejs.org/en) per poter utilizzare il gestore di pacchetti `npm`.

#### <img src="https://upload.wikimedia.org/wikipedia/commons/8/87/Windows_logo_-_2021.svg" alt="Windows Logo" width="12"/> Windows
Per poter funzionare correttamente su Windows, il modulo richiede l'installazione dei C++ Windows Tools. Pertanto, è necessario eseguire i seguenti comandi:
```bash
npm install --global windows-build-tools 
set PYTHON=%USERPROFILE%\\.windows-build-tools\\python27\\python.exe

npm install --legacy-peer-deps
npm audit fix --force
```
#### <img src="https://upload.wikimedia.org/wikipedia/commons/9/9e/UbuntuCoF.svg" alt="Ubuntu Logo" width="16"/> Ubuntu
In Ubuntu, invece, è sufficiente eseguire (Testato su Ubuntu 22.04):
```bash
npm install --legacy-peer-deps
npm audit fix --force
```

## 🚀 Esecuzione
Dopo l'installazione, per avviare il progetto del frontend è sufficiente su entrambi 
i sistemi operativi usare il comando:
```bash
npm start
```
