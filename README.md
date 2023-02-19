# MS3
MS3 - Medical Staff Shift Scheduler is designed to schedule medical shifts of hospital employees.

# Launch in a Container

Per avviare il sistema in container bisogna avere installato sulla macchina host docker e battere il seguente comando:
```
docker-compose up -d
```
Se si impiega il codice in produzione, è opportuno impostare le variabili di ambiente `DB_USER`, `DB_PASSWORD` e `DB_NAME` con i valori desiderati, altrimenti verranno utilizzati i valori di default presenti nel file `.env` che sono salvati in chiaro in questa repository, e ciò può rappresentare un problema di sicurezza.
La variabile di ambiente `FRONTEND_EXPOSE` determina su quale porta il server node ascolterà le richieste dei clients (Default is 8080).
