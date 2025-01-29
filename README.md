# MS3
MS3 - Medical Staff Shift Scheduler is designed to schedule medical shifts of hospital employees.

# Launch in a Container

Per avviare il sistema in container bisogna avere installato sulla macchina host docker e battere il seguente comando:
```
docker-compose up -d
```
Se si impiega il codice in produzione, è opportuno impostare le variabili di ambiente `DB_USER`, `DB_PASSWORD` e `DB_NAME` con i valori desiderati, altrimenti verranno utilizzati i valori di default presenti nel file `.env` che sono salvati in chiaro in questa repository, e ciò può rappresentare un problema di sicurezza.
La variabile di ambiente `FRONTEND_EXPOSE` determina su quale porta il server node ascolterà le richieste dei clients (Default is 8080).

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/90c0c1712b2141c2a3bfd8e243cd598a)](https://app.codacy.com/gh/CSW-Teams/MS3/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
