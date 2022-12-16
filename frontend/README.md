# MS3 FRONTEND
Avviare il progetto al primissimo lancio:
```bash
//Su windows
npm install --global windows-build-tools 
set PYTHON=%USERPROFILE%\\.windows-build-tools\\python27\\python.exe

npm install --legacy-peer-deps
npm audit fix --force
npm start
```
Avviare progetto le volte successive:
```bash
npm start
```

## Nota bene
Se il progetto viene lanciato correttemente ma non riesce a contattare il backend tramite API rest , sarebbe opportuno scaricare e attivare l'estensione per browser _Allow CORS: Access-Control-Allow-Origin_ .
