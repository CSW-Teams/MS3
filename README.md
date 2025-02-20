# 🏥 MS3
MS3 - Medical Staff Shift Scheduler is designed to schedule medical shifts of hospital employees.

## 🚀 Running the Project Locally
Installare [PostgreSQL](https://www.postgresql.org/) e creare nel DBMS:
* un utente con username = `sprintfloyd` e password = `sprintfloyd` con grants da **SUPERUSER**
* un database vuoto chiamato `ms3`
* 3 utenti:
  * `public_scheme_user` con password `password_public`
  * `tenant_a_user` con password `password_a`
  * `tenant_b_user` con password `password_b`
    * Ognuno con i seguenti grants:
      
      ```
      LOGIN
      NOSUPERUSER
      NOCREATEDB
      NOCREATEROLE
      INHERIT
      NOREPLICATION
      NOBYPASSRLS
      CONNECTION LIMIT -1
      ```

Per avviare il sistema, lanciare il Backend e il [Frontend](https://github.com/CSW-Teams/MS3/tree/main/frontend) e visitare
```
http://localhost:3000
```
(3000 è la porta di default)

## 📦 Running the project with Containers
Su **Windows**, la prima cosa da fare è convertire tramite il comando `dos2unix` i file di testo `mvnw` e `src/main/resources/db/init-scripts/init-users.sh`.

Per avviare il sistema in containers, bisogna avere installato sulla macchina host [Docker](https://www.docker.com/) e battere il seguente comando su terminale:
```
docker-compose up -d
```
Se si impiega il codice in produzione, è opportuno impostare le variabili di ambiente `DB_USER`, `DB_PASSWORD`, `DB_NAME`, `DB_TENANT_PUBLIC_USER`, `DB_TENANT_PUBLIC_PASSWORD`, `DB_TENANT_A_USER`, `DB_TENANT_A_PASSWORD`, `DB_TENANT_B_USER` e `DB_TENANT_B_PASSWORD` con i valori desiderati, altrimenti verranno utilizzati i valori di default presenti nel file `.env` che sono salvati in chiaro in questa repository, e ciò può rappresentare un problema di sicurezza.
La variabile di ambiente `FRONTEND_EXPOSE` determina su quale porta il server node ascolterà le richieste dei clients (Default is 8080).

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/90c0c1712b2141c2a3bfd8e243cd598a)](https://app.codacy.com/gh/CSW-Teams/MS3/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
