# Codacy — Manuale Utente Integrato (MS3)

> **Obiettivo**: fornire un manuale operativo completo su Codacy per sviluppatori e team di progetto, allineato alla Definition of Done (DoD) di MS3 e focalizzato su analisi statica, qualità del codice e integrazione con GitHub Pull Request.

---

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

Codacy è una piattaforma di **analisi statica del codice** e **code quality** che integra molteplici strumenti (linters, analyzer di manutenibilità, security scanner) per valutare automaticamente la qualità del codice sorgente.

Nel flusso di lavoro MS3, Codacy viene utilizzata per:
- prevenire **regressioni di qualità** tramite Quality Gate e PR Checks;
- applicare automaticamente la **Definition of Done**;
- uniformare lo **stile del codice**;
- individuare **code smells**, problemi di sicurezza e manutenibilità;
- monitorare l’evoluzione della qualità nel tempo.

---

## Concetti chiave

- **Project / Repository**: ogni repository GitHub analizzato corrisponde a un progetto Codacy.
- **Provider**: sistema di versionamento (GitHub) integrato con Codacy.
- **Analysis**: esecuzione dell’analisi statica che produce issue e metriche.
- **Issue**: violazione di una regola di qualità, stile, sicurezza o complessità.
- **Quality Gate**: insieme di criteri minimi che una PR deve rispettare.
- **Project Grade**: valutazione complessiva (A–F) della qualità del progetto.
- **PR Checks**: controlli automatici eseguiti sulle Pull Request.
- **New Issues Only**: principio secondo cui vengono valutati solo i problemi introdotti dal nuovo codice.

---

## Panoramica funzionalità

Codacy fornisce:
- dashboard di progetto con trend di qualità;
- lista delle issue con filtri per severità e categoria;
- Quality Gate configurabili;
- Code Patterns per stile e best practice;
- analisi di complessità ciclomatica;
- rilevamento di duplicazioni di codice;
- annotazioni automatiche nelle Pull Request;
- integrazione nativa con GitHub.

---

## Onboarding: registrazione e accesso via GitHub

1. Accedere a Codacy tramite autenticazione GitHub (OAuth).
2. Autorizzare l’accesso ai repository richiesti.
3. Selezionare l’organizzazione corretta.

> **Nota**: l’installazione dell’app Codacy su GitHub può richiedere permessi di amministratore.

---

## Collegare Codacy a un repository

1. Selezionare **Add Project** in Codacy.
2. Scegliere GitHub come provider.
3. Selezionare il repository del progetto.
4. Attendere il completamento della prima analisi, che costituisce la baseline iniziale.

---

## Impostazioni base di progetto e organizzazione

Le principali impostazioni includono:
- gestione di membri e permessi;
- attivazione dei tool di analisi;
- configurazione dei PR Checks;
- definizione dei Quality Gate.

**Best practice MS3**:
- PR Checks Codacy attivi su tutti i branch protetti;
- Quality Gate basati su nuove issue;
- enforcement della DoD tramite GitHub branch protection.

---

## Quality Gate, Project Grade e PR Checks (DoD)

### Quality Gate

Il **Quality Gate** definisce le condizioni minime che una Pull Request deve soddisfare per essere considerata accettabile.

Caratteristiche:
- viene valutato ad ogni PR;
- produce un esito binario (pass / fail);
- il risultato è inviato a GitHub come status check.

Criteri tipici:
- numero massimo di **nuove issue** oltre una certa severità;
- assenza di **nuove issue di sicurezza**;
- limiti su **complessità** e **duplicazione** introdotte;
- vincoli su **coverage** o **diff coverage**.

Il Quality Gate è progettato per lavorare **sul delta della PR**, non sull’intero codicebase.

---

### Quality Gate e codice legacy

Codacy distingue tra:
- issue già presenti prima della PR;
- issue introdotte dalla PR.

Configurando il Quality Gate sulle **new issues**, il debito tecnico storico:
- non blocca le PR;
- resta visibile;
- può essere gestito in modo incrementale.

---

### Project Grade

Il **Project Grade** è una valutazione globale (A–F) calcolata:
- sull’intero branch;
- considerando tutto il codice esistente;
- aggregando issue, complessità, duplicazione e coverage.

Proprietà chiave:
- non è PR-aware;
- non blocca le Pull Request;
- riflette la salute complessiva del progetto.

---

### Relazione tra Project Grade e Pull Request

Nel workflow MS3:
- il Project Grade è **osservazionale**;
- il Quality Gate è **prescrittivo e bloccante**.

Una PR può essere accettata anche con Project Grade basso, purché non introduca regressioni.

---

### PR Checks su GitHub

I **PR Checks Codacy**:
- vengono eseguiti automaticamente;
- compaiono come status check su GitHub;
- riflettono l’esito del Quality Gate.

I PR Checks possono essere:
- **informativi** (merge consentito);
- **bloccanti** (merge impedito).

La configurazione avviene tramite **branch protection rules di GitHub**, non da Codacy.

**Best practice MS3**:
- PR Checks Codacy configurati come *required*;
- merge consentito solo con status verde.

---

## Code Patterns e stile del codice (DoD)

I **Code Patterns** definiscono regole di stile, sicurezza e best practice.

- ogni violazione genera una issue;
- le issue influenzano i PR Checks;
- le regole vanno modificate solo previo consenso del team.

---

## Clean Code: complessità, duplicazione e commented code (DoD)

### Complessità ciclomatica
Misura la complessità del flusso logico del codice.

**Refactoring consigliato**:
- suddividere funzioni complesse;
- ridurre nesting;
- estrarre metodi.

---

### Duplicazione
La duplicazione riduce la manutenibilità.

**Azioni consigliate**:
- estrarre logica comune;
- evitare copia-incolla.

---

### Commented-out code
Il codice commentato non deve restare nel repository.

**DoD**:
- rimuovere codice commentato;
- affidarsi al version control per lo storico.

---

## Workflow quotidiano: sviluppatore e team

### Sviluppatore
1. Implementa feature o fix.
2. Apre Pull Request.
3. Analizza PR Checks Codacy.
4. Risolve eventuali nuove issue.
5. Verifica il superamento del Quality Gate.

---

### Team
- monitorare trend di qualità;
- valutare rumore delle regole;
- pianificare refactoring incrementali.

---

## Triage e analisi delle issue

Per ogni issue valutare:
- severità;
- categoria;
- contesto;
- impatto sul codice.

Utilizzare filtri:
- New Issues;
- Branch;
- Categoria.

---

## Gestione del legacy: “New Issues Only” e baseline

### Il problema del legacy

Il codice legacy:
- influisce sul Project Grade;
- può generare molte issue;
- non deve bloccare lo sviluppo corrente.

---

### Principio “New Issues Only”

Principio operativo:
> Il codice nuovo non deve peggiorare la qualità esistente.

In pratica:
- le PR sono valutate solo sulle issue introdotte;
- le issue pre-esistenti non fanno fallire il Quality Gate;
- il miglioramento è incrementale.

---

### Issue potenziali su codice non modificato

Codacy può segnalare **potential issues**:
- presenti in linee non modificate;
- emerse indirettamente dalla PR.

Queste issue:
- non sono considerate nuove;
- non bloccano la PR;
- rappresentano debito tecnico noto.

---

### Modifiche su file legacy

Quando una PR tocca codice legacy:
- nuove issue → da correggere;
- issue rimosse → miglioramento;
- issue esistenti → accettabili.

---

### Baseline

Codacy utilizza il **default branch** come baseline implicita:
- il confronto avviene rispetto allo stato precedente;
- non è necessario un freeze manuale.

---

## Ignorare o sopprimere issue e falsi positivi

### Ignorare singole issue

È possibile ignorare manualmente una issue:
- dalla lista issue;
- con motivazione tracciabile.

Una issue ignorata:
- non influisce su Quality Gate;
- resta documentata come decisione tecnica.

---

### Ignorare issue in blocco: limiti

Codacy **non fornisce un comando “ignora tutto”**.

Questo garantisce:
- tracciabilità;
- responsabilità tecnica.

---

### Strategie per il legacy

Approcci consigliati:
1. alzare la severità del Quality Gate;
2. disabilitare pattern non rilevanti;
3. ignorare file o directory legacy;
4. pulizia incrementale del debito tecnico.

---

### Impatto sulla DoD

Nel modello MS3:
- ignorare un’issue è una decisione consapevole;
- la DoD resta applicata al codice nuovo;
- la qualità migliora nel tempo.

---

## Risoluzione problemi comuni

- PR bloccata → verificare nuove issue e severità.
- Troppo rumore → rivedere Code Patterns.
- Project Grade basso → effetto del legacy, non della PR.

---

## Note su piani, limiti e coerenza con Codacy

- Le funzionalità dipendono dal piano Codacy.
- Alcune metriche (es. coverage) richiedono integrazioni esterne.
- La configurazione va mantenuta coerente con la DoD del team.

---

## Conclusione

L’uso combinato di:
- Quality Gate basati su *new issues*,
- PR Checks bloccanti,
- gestione consapevole del legacy,

permette di applicare Codacy in modo **realistico, sostenibile e incrementale**, anche su progetti con storico significativo.
