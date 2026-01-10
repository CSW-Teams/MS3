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
Codacy è una piattaforma di **analisi statica** e **code quality** che integra più strumenti (linters, code analyzers, security scanners) per valutare automaticamente la qualità del codice.

Nel flusso di MS3 è utilizzata per:
- **Prevenire regressioni** di qualità tramite Quality Gate e PR Checks.
- **Bloccare PR non conformi** ai requisiti di qualità.
- **Uniformare lo stile** del codice tramite Code Patterns.
- **Misurare complessità, duplicazioni e code smells**.
- **Monitorare la qualità nel tempo** tramite il Project Grade.

---

## Concetti chiave
- **Project / Repository**: ogni repository analizzato corrisponde a un progetto Codacy.
- **Provider**: integrazione con GitHub per sincronizzazione e PR checks.
- **Analysis**: esecuzione dell’analisi statica che produce issue, metriche e grade.
- **Issues**: violazioni di qualità, stile, sicurezza o manutenibilità.
- **Quality Gate**: criteri minimi di qualità che una PR deve rispettare.
- **Project Grade**: valutazione complessiva (A–F) della qualità del progetto.
- **PR Checks**: verifiche automatiche visibili nelle Pull Request GitHub.
- **New Issues Only**: approccio che valuta solo i problemi introdotti di recente.

---

## Panoramica funzionalità
Le principali funzionalità offerte da Codacy includono:
- Dashboard di progetto con stato e trend qualità.
- Lista delle issue con filtri per severità e categoria.
- Quality Gate configurabili.
- Code Patterns per stile e best practice.
- Analisi di complessità ciclomatica.
- Rilevamento duplicazioni di codice.
- Annotazioni automatiche nelle Pull Request.
- Integrazione nativa con GitHub.

---

## Onboarding: registrazione e accesso via GitHub
1. Accedere a Codacy tramite GitHub (OAuth).
2. Autorizzare l’accesso ai repository richiesti.
3. Selezionare l’organizzazione corretta.

> Nota: l’installazione dell’app Codacy su GitHub può richiedere permessi di amministratore.

---

## Collegare Codacy a un repository
1. Selezionare **Add Project** in Codacy.
2. Scegliere GitHub come provider.
3. Selezionare il repository MS3.
4. Attendere il completamento della prima analisi (baseline iniziale).

---

## Impostazioni base di progetto e organizzazione
Le impostazioni principali includono:
- Gestione membri e permessi.
- Configurazione tool di analisi.
- Attivazione PR checks.
- Definizione Quality Gate.

**Best practice MS3**:
- PR checks Codacy obbligatori.
- Tool attivi per style, maintainability, security e duplication.

---

## Quality Gate, Project Grade e PR Checks (DoD)

### Quality Gate
Il **Quality Gate** definisce le condizioni minime che una PR deve soddisfare per essere considerata accettabile.

Tipici criteri:
- Nessuna nuova issue critica.
- Duplicazione sotto soglia.
- Complessità entro limiti accettabili.

Il Quality Gate è **bloccante**: se fallisce, la PR non può essere mergiata.

---

### Project Grade: significato e modello di calcolo

Il **Project Grade** è una valutazione sintetica della qualità complessiva del codice di un progetto, espressa da **A** a **F**.

Il grade è calcolato:
- per branch,
- sull’intero codice analizzato,
- come aggregazione ponderata di più metriche.

Contribuiscono al grade:
- Numero e severità delle issue.
- Densità di issue rispetto alle linee di codice.
- Complessità ciclomatica.
- Duplicazione del codice.
- Copertura dei test (se presente).

Il Project Grade rappresenta quindi **la salute strutturale del progetto**, non la qualità di una singola PR.

---

### Relazione tra Project Grade e Pull Request

- Il Project Grade **non è un criterio diretto di accettazione** di una PR.
- Le PR sono valutate tramite:
  - Quality Gate,
  - PR Checks,
  - nuove issue introdotte.

Un progetto può avere un Project Grade medio-basso e accettare PR corrette, purché **non introducano regressioni**.

---

### PR Checks
I PR Checks Codacy:
- vengono eseguiti automaticamente su ogni PR,
- mostrano lo stato direttamente su GitHub,
- devono risultare **verdi** per il merge.

---

## Code Patterns e stile del codice (DoD)
I Code Patterns definiscono regole di stile, sicurezza e best practice.

- Le violazioni generano issue.
- Le issue compaiono nei PR Checks.
- Le regole vanno modificate solo con consenso del team.

---

## Clean Code: complessità, duplicazione e commented code (DoD)

### Complessità ciclomatica
Indica il livello di ramificazione logica del codice.

**Refactoring consigliato**:
- suddividere funzioni complesse,
- ridurre nesting,
- estrarre metodi.

---

### Duplicazione
Il codice duplicato riduce manutenibilità e qualità.

**Azioni consigliate**:
- estrarre logica comune,
- evitare copia-incolla.

---

### Commented-out code
Il codice commentato non deve restare nel repository.

**DoD**:
- rimuovere codice commentato,
- usare version control per lo storico.

---

## Workflow quotidiano: sviluppatore e team

### Sviluppatore
1. Implementa feature o fix.
2. Apre PR.
3. Verifica PR Checks Codacy.
4. Risolve eventuali nuove issue.
5. Assicura Quality Gate superato.

---

### Team
- Monitorare trend di qualità.
- Valutare rumore delle regole.
- Pianificare refactoring incrementali.

---

## Triage e analisi delle issue
Per ogni issue valutare:
- Severità.
- Categoria.
- Contesto.
- Responsabilità.

Usare filtri:
- New Issues.
- Branch.
- Categoria.

---

## Gestione del legacy: “New Issues Only” e baseline

### Il problema del legacy
Il codice legacy influisce sul Project Grade perché il grade considera **tutto il codice esistente**.

Questo non rende inutile la metrica, ma richiede **una corretta interpretazione**.

---

### Strategia MS3
MS3 separa:
- **controllo di qualità** (PR, bloccante),
- **misurazione di qualità** (Project Grade, osservazionale).

---

### “New Issues Only”
Principio operativo:
> Il codice nuovo non deve peggiorare la qualità esistente.

- Le PR sono valutate solo sulle nuove issue.
- Il debito tecnico storico non blocca lo sviluppo.

---

### Modifiche su codice legacy
- Nuove issue → da correggere.
- Issue rimosse → miglioramento.
- Issue esistenti → accettabili, ma opportunità di refactoring.

---

### Baseline
Se disponibile:
- il default branch diventa riferimento iniziale.

Se non disponibile:
- Quality Gate orientato ai file modificati.

In entrambi i casi:
- miglioramento graduale
