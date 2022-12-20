# MS3 FRONTEND
### Installazione
##### Windows
Per poter funzionare correttamente su Windows, il modulo richiede l'installazione dei C++ Windows Tools. Pertanto, è necessario eseguire i seguenti comandi:
```bash
npm install --global windows-build-tools 
set PYTHON=%USERPROFILE%\\.windows-build-tools\\python27\\python.exe

npm install --legacy-peer-deps
npm audit fix --force
```
##### Ubuntu
In Ubuntu, invece, è sufficiente eseguire (Testato su Ubuntu 22.04):
```bash
npm install --legacy-peer-deps
npm audit fix --force
```

### Esecuzione
Dopo aver installato correttamente il progetto, è sufficiente avviare il progetto su entrambi i sistemi operativi mediante il comando:
```bash
npm start
```

## Nota bene
Se il progetto viene lanciato correttemente ma non riesce a contattare il backend tramite API rest , sarebbe opportuno scaricare e attivare l'estensione per browser _Allow CORS: Access-Control-Allow-Origin_ .
