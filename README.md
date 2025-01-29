# MS3

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/dff94f086a134d558ef884b74da0aea8)](https://app.codacy.com/gh/CSW-Teams/MS3?utm_source=github.com&utm_medium=referral&utm_content=CSW-Teams/MS3&utm_campaign=Badge_Grade)

MS3 - Medical Staff Shift Scheduler is designed to schedule medical shifts of hospital employees.

# Launch in a Container

Per avviare il sistema in container bisogna avere installato sulla macchina host docker e battere il seguente comando:
```
docker-compose up -d
```
Se si impiega il codice in produzione, è opportuno impostare le variabili di ambiente `DB_USER`, `DB_PASSWORD` e `DB_NAME` con i valori desiderati, altrimenti verranno utilizzati i valori di default presenti nel file `.env` che sono salvati in chiaro in questa repository, e ciò può rappresentare un problema di sicurezza.
La variabile di ambiente `FRONTEND_EXPOSE` determina su quale porta il server node ascolterà le richieste dei clients (Default is 8080).

### [Codacy Analyses](https://app.codacy.com/gh/CSW-Teams/MS3/issues/current)
