# Codacy — Manuale Utente Integrato (MS3)

> **Obiettivo**: fornire un manuale operativo completo su Codacy per sviluppatori e team di progetto, allineato alla Definition of Done (DoD) di MS3 e focalizzato su analisi statica, qualità del codice e integrazione con GitHub Pull Request.

## Indice
1. [Cos’è Codacy e perché viene usato](#cosè-codacy-e-perché-viene-usato)
2. [Concetti chiave](#concetti-chiave)
3. [Panoramica funzionalità](#panoramica-funzionalità)
4. [Onboarding: registrazione e accesso via GitHub](#onboarding-registrazione-e-accesso-via-github)
5. [Collegare Codacy a un repository](#collegare-codacy-a-un-repository)
6. [Impostazioni base di progetto e organizzazione](#impostazioni-base-di-progetto-e-organizzazione)
7. [Quality Gate, Project Grade e PR Checks (DoD)](#quality-gate-project-grade-e-pr-checks-dod)
8. [Code Patterns e stile del codice (DoD)](#code-patterns-e-stile-del-codice-dod)
9. [Clean Code: complessità, duplicazione e commented code (DoD)](#clean-code-complessità-duplicazione-e-commented-code-dod)
10. [Workflow quotidiano: sviluppatore e team](#workflow-quotidiano-sviluppatore-e-team)
11. [Triage e analisi delle issue](#triage-e-analisi-delle-issue)
12. [Gestione del legacy: “New Issues Only” e baseline](#gestione-del-legacy-new-issues-only-e-baseline)
13. [Ignorare o sopprimere issue e falsi positivi](#ignorare-o-sopprimere-issue-e-falsi-positivi)
14. [Risoluzione problemi comuni](#risoluzione-problemi-comuni)
15. [Note su piani, limiti e coerenza con Codacy](#note-su-piani-limiti-e-coerenza-con-codacy)

---

## Cos’è Codacy e perché viene usato
Codacy è una piattaforma di **analisi statica** e **code quality** che integra più strumenti (linters, code analyzers, security scanners) per valutare automaticamente la qualità del codice. Nel flusso di MS3 è utilizzata per:
- **Prevenire regressioni** di qualità (via Quality Gate e Project Grade).
- **Bloccare PR non conformi** tramite i check su GitHub.
- **Uniformare lo stile** con Code Patterns.
- **Misurare complessità, duplicazioni e code smells**.

---

## Concetti chiave
- **Project/Repository**: ogni repository analizzato è un progetto Codacy.
- **Provider**: l’integrazione con GitHub (o altri provider) che permette sincronizzazione e PR checks.
- **Analysis**: analisi statica automatica del codice che produce issue, metriche e grade.
- **Issues**: problemi di qualità, stile, sicurezza o maintainability rilevati dagli strumenti.
- **Quality Gate**: criteri di qualità minimi da rispettare per superare i check.
- **Project Grade**: punteggio complessivo (A–F) della qualità del progetto.
- **PR Checks**: status checks su GitHub che indicano se la PR è conforme ai requisiti.
- **New Issues Only**: focalizzazione sui problemi introdotti di recente (baseline).

---

## Panoramica funzionalità
Le funzionalità principali di Codacy includono:
- **Dashboard di progetto**: stato generale, grade, trend e quick links.
- **Issues list**: elenco e dettaglio delle issue con filtri per severità e categoria.
- **Quality Gate**: definizione e verifica dei criteri di qualità.
- **Code Patterns**: regole di stile, best practice e sicurezza.
- **Duplicazione e complessità**: metriche di duplicazione e complessità ciclomatica.
- **PR analysis**: annotazioni e check automatici sulle Pull Request.
- **Security findings**: rilevazioni di vulnerabilità o pattern insicuri.
- **Integrazioni**: GitHub, notifiche e webhook.

---

## Onboarding: registrazione e accesso via GitHub
1. **Accedi a Codacy** tramite GitHub (OAuth).
2. **Autorizza l’accesso** a repository e organizzazioni richiesti (in base ai permessi).
3. **Completa il profilo** Codacy, scegliendo l’organizzazione corretta.

> Nota: a seconda della policy aziendale, l’accesso potrebbe richiedere l’installazione dell’app Codacy su GitHub da parte di un admin.

---

## Collegare Codacy a un repository
1. In Codacy, seleziona **Add Project** (o equivalente nella UI attuale).
2. Scegli il **provider GitHub** e l’organizzazione corretta.
3. Seleziona il repository **MS3** da importare.
4. Attendi la prima **analisi iniziale** (baseline).

> Suggerimento: la prima analisi può richiedere tempo a seconda della dimensione del repository e della configurazione dei tool.

---

## Impostazioni base di progetto e organizzazione
Le impostazioni di base (posizione esatta nella UI può variare) includono:
- **Organization settings**: membri, ruoli, permessi e integrazioni.
- **Project settings**: configurazione dei tool, language support, quality gate e notifications.
- **PR integration**: attivazione dei check automatici sulle pull request.

**Best practice consigliate per MS3**:
- Verificare che l’integrazione GitHub sia attiva e che i **status checks** siano obbligatori.
- Mantenere attivo il set di tool che copre **style**, **maintainability**, **security** e **duplication**.

---

## Quality Gate, Project Grade e PR Checks (DoD)
Questa sezione mappa direttamente i requisiti di DoD relativi a Codacy.

### ✅ Quality Gate deve passare
- **Significato**: il Quality Gate definisce criteri minimi (es. assenza di nuove issue critiche, soglia di duplicazione, limiti di complessità).
- **Dove vederlo**:
  - **Codacy UI**: nella sezione Quality Gate o nel dashboard del progetto.
  - **GitHub PR Checks**: come status check automatico in “Checks”.
- **Cause comuni di fallimento**:
  - Nuove issue di severità alta.
  - Superamento soglia di duplicazione.
  - Complessità elevata o code smells.
- **Come rimediare**:
  1. Aprire la lista issue e filtrare per **New Issues**.
  2. Correggere il codice o rifattorizzare.
  3. Rieseguire la pipeline/PR check.

### ✅ Project Grade ≥ D
- **Significato**: il grade è un voto complessivo (A–F) calcolato da Codacy.
- **Dove vederlo**: dashboard di progetto e summary della qualità.
- **Come prevenire regressioni**:
  - Evitare di introdurre nuove issue.
  - Mantenere bassa la duplicazione.
  - Ridurre complessità in nuove modifiche.
- **Cosa fare se scende**:
  - Analizzare metriche di qualità e lista issue.
  - Identificare moduli con molte violazioni o peggioramenti.

### ✅ GitHub PR Checks devono essere verdi
- **Significato**: Codacy pubblica un check che deve essere “success”.
- **Segnali principali**:
  - Superamento Quality Gate.
  - Assenza di issue bloccanti nei file modificati.
- **Come reagire a un check fallito**:
  1. Aprire il dettaglio del check in GitHub.
  2. Seguire il link a Codacy per vedere le issue.
  3. Correggere o giustificare (se permesso) e rieseguire.

---

## Code Patterns e stile del codice (DoD)
I **Code Patterns** sono regole di stile e best practice definite da tool specifici.

- **Configurazione**: in genere nella sezione Project Settings > Code Patterns.
- **Output**: le violazioni appaiono come issue e possono essere visibili nei PR checks.
- **Come agire**:
  - Correggere la regola violata (es. naming, formattazione, best practice).
  - Evitare di “sopprimere” pattern senza motivazione.

---

## Clean Code: complessità, duplicazione e commented code (DoD)

### Complessità ciclomatica
- Codacy rileva la **complessità** tramite tool integrati (es. PMD/SpotBugs o equivalenti).
- **Come interpretare**: issue di complessità alta indicano metodi difficili da mantenere o testare.
- **Refactoring consigliato**:
  - Spezzare funzioni lunghe.
  - Ridurre branching annidato.
  - Estrarre metodi e classi.

### Duplicazione
- Codacy rileva **duplicazioni** e può applicare soglie nel Quality Gate.
- **Dove vederla**: metriche di duplicazione e issue associate.
- **Come intervenire**:
  - Estrarre codice comune.
  - Evitare copia-incolla e creare utility condivise.

### Commented-out code
- Codacy può rilevare porzioni di codice commentato in base ai tool attivi.
- **Aspettativa DoD**: il codice commentato non deve restare nel repository.
- **Come intervenire**:
  - Rimuovere il codice commentato.
  - Se serve conservarlo, usare strumenti di versioning o documentazione, non commenti nel codice.

---

## Workflow quotidiano: sviluppatore e team

### Sviluppatore (giornaliero)
1. Lavorare su feature/bugfix.
2. Prima della PR, controllare eventuali warning locali (lint, test).
3. Aprire PR e verificare i **Codacy checks**.
4. Risolvere le issue segnalate (focus su **New Issues**).
5. Assicurarsi che Quality Gate e Project Grade non peggiorino.

### Team (settimanale/sprint)
- Monitorare dashboard Codacy e trend qualità.
- Rivedere regole/patterns troppo rumorosi.
- Pianificare task di refactoring per ridurre complessità e duplicazione.

---

## Triage e analisi delle issue
Per ogni issue:
1. **Severità**: alta/critica vs media/bassa.
2. **Categoria**: style, maintainability, security, duplication.
3. **Contesto**: file coinvolti, commit recente.
4. **Assegnazione**: responsabile del modulo.

Strumenti utili:
- Filtri “New Issues”.
- Filtri per branch (default branch vs feature).
- Cronologia dell’issue.

---

## Gestione del legacy: “New Issues Only” e baseline

### “New Issues Only”
- **Concetto**: la qualità viene misurata solo sulle issue introdotte dopo una baseline iniziale.
- **Vantaggi**: evita di bloccare PR per debito tecnico storico.
- **Workflow atteso**:
  1. Stabilire una baseline iniziale.
  2. Imporre che ogni PR non aggiunga nuove issue.
  3. Ridurre gradualmente il debito tecnico con iniziative dedicate.

### Baseline / “ignore existing issues”
Codacy supporta il concetto di **baseline** (o “ignore existing issues”) in modo variabile a seconda del piano e della configurazione.

**Se disponibile**:
1. Impostare una baseline dalla situazione attuale del default branch.
2. Abilitare la modalità “New Issues Only” per i check.

**Se non disponibile**:
- Usare Quality Gate orientati ai soli file modificati in PR.
- Gestire il debito storico con backlog dedicato, mantenendo i check su nuove issue.

---

## Ignorare o sopprimere issue e falsi positivi
- Codacy permette di **ignorarle** o **sopprimerle** in base ai tool e ai permessi.
- Usare l’ignore solo quando:
  - l’issue è un falso positivo documentato,
  - la regola non è applicabile al contesto.
- Documentare sempre la motivazione.

> Nota: le modalità di ignore variano per tool e piano; consultare le opzioni del progetto.

---

## Risoluzione problemi comuni

### PR check non compare su GitHub
Possibili cause:
- App Codacy non installata su GitHub.
- Permessi mancanti sul repository.
- Integrazione disattivata nelle impostazioni.

**Azioni**:
1. Verificare installazione dell’app.
2. Controllare che il repository sia incluso nell’integrazione.
3. Riconfigurare l’integrazione in Codacy.

### Nessun risultato di analisi in Codacy
Possibili cause:
- Webhook non attivo.
- Branch non monitorato.
- Analisi fallita o non avviata.

**Azioni**:
1. Controllare lo stato dei webhook su GitHub.
2. Verificare che il branch di default sia monitorato.
3. Forzare una nuova analisi (nuovo commit o re-run).

### Quality Gate fallito
- Identificare le issue “New Issues”.
- Correggere i file modificati.
- Rivalutare la PR.

### Regole troppo rumorose
- Discutere in team se disabilitare o ridurre severità.
- Preferire la correzione o la disattivazione mirata.

---

## Note su piani, limiti e coerenza con Codacy
- Alcune funzionalità (baseline, advanced security, policy gating) possono dipendere dal **piano Codacy**.
- La **terminologia e la UI** possono variare nel tempo: verificare sempre la sezione di aiuto ufficiale.
- Evitare di introdurre configurazioni o segreti nel repository: usare sistemi di gestione secure (GitHub Secrets o secret manager).

---

## Checklist DoD (Codacy)
- [ ] Quality Gate **passato** su PR.
- [ ] Project Grade **≥ D**.
- [ ] PR Check Codacy **verde** in GitHub.
- [ ] Nessuna violazione di Code Patterns rilevante.
- [ ] Complessità entro soglie accettate.
- [ ] Duplicazione sotto soglia Quality Gate.
- [ ] Nessun codice commentato o duplicato introdotto.

---

### Glossario rapido
- **Quality Gate**: set di regole che decide se la qualità è accettabile.
- **Project Grade**: voto complessivo (A–F).
- **New Issues Only**: controllo solo su problemi introdotti di recente.
- **PR Checks**: verifiche automatiche sulla Pull Request.

