# Manuale test 2FA (TOTP)

Questa guida spiega **passo‑passo** come eseguire i test relativi alla funzionalità 2FA appena implementata. È pensata per chi lo fa **per la prima volta**.

## 1) Quali test 2FA esistono

### Backend (Java, JUnit)
- `src/test/java/org/cswteams/ms3/security/TwoFactorAuthenticationServiceTest.java`
- `src/test/java/org/cswteams/ms3/rest/TwoFactorRestEndpointTest.java`

### Frontend (React, Jest)
- `frontend/src/views/utente/LoginView.test.js`
- `frontend/src/views/utente/TwoFactorEnrollmentView.test.js`

## 2) Prerequisiti minimi

### Backend
- Java 11
- Maven (o wrapper `./mvnw`)

### Frontend
- Node.js + npm
- Dipendenze installate nella cartella `frontend/`

### Variabili 2FA (solo se lanci l’app completa o test integrati)
Da `README.md`:
- `HMAC_MASTER_KEY`
- `MAX_OTP_ATTEMPTS`
- `OTP_LOCKOUT_SECONDS`
- `ENFORCED_2FA_ROLES`
- `RECOVERY_CODE_COUNT`

## 3) Eseguire i test backend 2FA (passo‑passo)

### 3.1 Apri un terminale nella root del progetto
Percorso: `/workspace/MS3`

### 3.2 (Opzionale) Verifica Java 11
Se hai più versioni, assicurati che `JAVA_HOME` punti a Java 11.

### 3.3 Lancia i test 2FA backend (singolarmente)
Consigliato per la prima volta:

```bash
./mvnw -Dtest=TwoFactorAuthenticationServiceTest test
./mvnw -Dtest=TwoFactorRestEndpointTest test
```

> Questi test **non richiedono** un database perché usano mock o logica in‑memory.

## 4) (Solo se vuoi lanciare l’intera suite backend con DB)
Alcuni test di integrazione richiedono Postgres e lo `SchemasInitializer`.

### 4.1 Avvia un Postgres “usa e getta”
Esempio (vedi anche `docs/testing/db-bootstrap.md`):

```bash
docker run --name ms3-test-db \
  -e POSTGRES_DB=${DB_NAME:-ms3} \
  -e POSTGRES_USER=${DB_USER:-sprintfloyd} \
  -e POSTGRES_PASSWORD=${DB_PASSWORD:-sprintfloyd} \
  -e DB_TENANT_PUBLIC_USER=${DB_TENANT_PUBLIC_USER:-public_scheme_user} \
  -e DB_TENANT_PUBLIC_PASSWORD=${DB_TENANT_PUBLIC_PASSWORD:-password_public} \
  -e DB_TENANT_A_USER=${DB_TENANT_A_USER:-tenant_a_user} \
  -e DB_TENANT_A_PASSWORD=${DB_TENANT_A_PASSWORD:-password_a} \
  -e DB_TENANT_B_USER=${DB_TENANT_B_USER:-tenant_b_user} \
  -e DB_TENANT_B_PASSWORD=${DB_TENANT_B_PASSWORD:-password_b} \
  -p 5432:5432 -d postgres:14
```

### 4.2 Esegui i test con i profili corretti

```bash
SPRING_PROFILES_ACTIVE=container,test ./mvnw -DskipFrontendTests=true test
```

## 5) Eseguire i test frontend 2FA (passo‑passo)

### 5.1 Entra nella cartella frontend

```bash
cd frontend
```

### 5.2 Installa le dipendenze (prima volta)

```bash
npm install
```

### 5.3 Esegui solo i test 2FA
Per evitare la modalità watch, usa `CI=1`:

```bash
CI=1 npm test -- --runTestsByPath \
  src/views/utente/LoginView.test.js \
  src/views/utente/TwoFactorEnrollmentView.test.js
```

## 6) Come interpretare l’output

### Backend
- **Successo**: `BUILD SUCCESS` e test verdi.
- **Errore**: stack trace con riferimento al test (es. `TwoFactorAuthenticationServiceTest`).

### Frontend
- **Successo**: output Jest con `PASS`.
- **Errore**: dettagli del test e delle asserzioni fallite.

## 7) Checklist rapida “prima esecuzione”
- [ ] Sei nella root del progetto?
- [ ] Hai Java 11 e Maven?
- [ ] Hai Node.js + npm?
- [ ] Hai eseguito `npm install` in `frontend/`?
- [ ] Stai lanciando **solo** i test 2FA?

## 8) Dove cercare se qualcosa fallisce
- Backend: `TwoFactorAuthenticationServiceTest`, `TwoFactorRestEndpointTest`
- Frontend: `LoginView.test.js`, `TwoFactorEnrollmentView.test.js`
- Setup DB test integrati: `docs/testing/db-bootstrap.md`
