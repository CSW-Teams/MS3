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
16. [Approfondimento tecnico: Project Grade e impatto del codice legacy](#approfondimento-tecnico-project-grade-e-impatto-del-codice-legacy)
17. [Approfondimento tecnico: Quality Gates e PR Checks](#approfondimento-tecnico-quality-gates-e-pr-checks)
18. [Approfondimento tecnico: Code Patterns abilitati e disabilitati in MS3](#approfondimento-tecnico-code-patterns-abilitati-e-disabilitati-in-ms3)

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

## Approfondimento tecnico: Project Grade e impatto del codice legacy

### Cos’è realmente il Project Grade
Il **Project Grade** è un indicatore sintetico della qualità complessiva del codice presente in un branch.  
Non rappresenta la qualità di una singola PR, ma lo stato globale del progetto in un determinato momento.

Il grade è calcolato come aggregazione ponderata di:
- numero e severità delle issue,
- densità delle issue rispetto alle linee di codice,
- complessità ciclomatica,
- duplicazione del codice,
- copertura dei test (se configurata).

---

### Perché il codice legacy influisce sul grade
Il Project Grade considera **tutto il codice analizzato**.  
Di conseguenza:
- issue storiche continuano a pesare sul risultato,
- il codice nuovo, anche se di alta qualità, migliora il grade solo in modo progressivo.

Questo comportamento è intenzionale: il grade misura il **debito tecnico complessivo**.

---

### Interpretazione corretta nel contesto MS3
Nel workflow MS3:
- il Project Grade è **una metrica osservazionale**,
- i Quality Gate e i PR Checks sono **meccanismi di controllo bloccanti**.

Un Project Grade basso **non invalida** una PR corretta, purché:
- non introduca nuove issue,
- rispetti i Quality Gate.

---

### Strategia consigliata
- Usare **New Issues Only** come protezione del codice nuovo.
- Accettare che il grade rifletta il legacy.
- Migliorare il grade in modo incrementale quando si interviene su file legacy.
- Evitare “reset” artificiali del grade tramite ignore indiscriminati.

---

### Modifiche su file legacy
Quando si modifica codice legacy:
- nuove issue → **da correggere**,
- issue rimosse → **miglioramento misurabile**,
- issue preesistenti → **non bloccanti**, ma visibili.

Questo approccio rende il Project Grade utile e onesto.

---

> **Nota interpretativa**
>  
> Il Project Grade non è un criterio di accettazione di una Pull Request.  
> È un indicatore di qualità complessiva e di evoluzione del progetto nel tempo.
> In presenza di legacy, il valore del grade sta nella sua traiettoria, non nel numero assoluto.

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

---

## Approfondimento tecnico: Quality Gates e PR Checks

### 1) Quality Gate: cosa viene valutato davvero (focus PR / delta)

Nel contesto delle Pull Request, l’obiettivo pratico del Quality Gate è **impedire regressioni**, cioè evitare che una PR introduca nuova “sporcizia” nel codebase.

Concettualmente, un Quality Gate può includere regole del tipo:
- **New issues over N**: fallisci se il numero di *nuove issue* oltre una certa severità supera una soglia.
- **New security issues over N**: fallisci se introduci nuove issue categorizzate come sicurezza.
- **Complexity is over X**: fallisci se la complessità introdotta (o rilevata nelle aree toccate) supera una soglia.
- **Duplication is over X**: fallisci se aumenti la duplicazione oltre soglie concordate.
- **Coverage variation / Diff coverage**: fallisci se la coverage scende (variazione) o se la coverage delle linee modificate (“diff”) è sotto soglia.

Nota operativa: anche quando nel progetto esistono molte issue storiche, un gate ben impostato **deve puntare alle new issues**, altrimenti la piattaforma diventa inutilizzabile su codebase legacy.

---

### 2) Come “ignorare il vecchio” nel PR check (senza cambiare il codice legacy)

Il requisito “controllare solo il nuovo codice in PR” non si ottiene con un pulsante “ignora tutto”, ma con una combinazione di scelte:

- **Quality Gate basato su nuove issue**: è la leva principale.
- **Severità mirata**: in fase di adozione, spesso ha senso bloccare solo Major/Critical (e Security), lasciando i Minor come rumore informativo.
- **Accettazione del Project Grade come osservazionale**: il grade riflette il legacy, non deve essere usato per bloccare PR.

In breve: **si isola il controllo PR** (bloccante) dal debito tecnico storico (osservazionale).

---

### 3) PR Checks su GitHub: informativi vs bloccanti

Codacy pubblica su GitHub uno o più **status check** associati alla PR (tab “Checks”). Il comportamento “blocca/non blocca” **non dipende da Codacy**, ma da GitHub:

- **Informativo**: il check può risultare rosso/giallo, ma GitHub permette il merge.
- **Bloccante**: GitHub impedisce il merge se il check non è “success”.

Per rendere i check bloccanti:
- configurare una **Branch Protection Rule** (o Ruleset) su GitHub;
- aggiungere i check Codacy tra i **Required status checks** per il branch protetto.

Best practice: introdurre inizialmente i check come informativi (per tarare rumore e soglie), poi renderli required quando il team è allineato sulla policy.

---

### 4) Perché a volte Codacy mostra issue “non nuove” in una PR (Potential Issues)

Durante l’analisi di PR, Codacy può evidenziare issue che non sono state introdotte dalla PR, spesso percepite come “rumore” su legacy.

Tipico scenario:
- la PR modifica una porzione di file legacy e la reportistica mostra anche issue “in prossimità” o collegate al contesto;
- vengono segnalate come **potential issues** o comunque come issue non strettamente parte del delta.

Interpretazione corretta:
- non sono *new issues* (se il sistema le classifica correttamente);
- non dovrebbero far fallire un Quality Gate basato su nuove issue;
- sono indicatori del debito tecnico attorno all’area toccata (utile per refactoring opportunistico).

---

### 5) Esiste un modo “rapido” (1 click) per ignorare tutte le vecchie issue?

In generale **no**: Codacy non è pensato per “azzerare” il debito tecnico con un singolo click, perché ignorare issue è una decisione tecnica che deve restare tracciabile.

Detto questo, esistono “leve” operative per gestire grandi quantità di legacy:

#### 5.1 Ignorare una singola issue (click + motivazione)
- utile per falsi positivi o issue note che non si intende risolvere a breve;
- richiede motivazione (buona pratica: documentarla).

#### 5.2 Disabilitare un pattern (soppressione per regola)
- utile se una regola produce rumore sistemico e non è considerata rilevante per la DoD;
- impatta **anche il nuovo codice**, quindi va deciso a livello team.

#### 5.3 Ignorare file o directory (esclusione dall’analisi)
- utile per codice generato, terze parti, legacy “frozen”;
- impatta **tutte** le issue in quel file, anche future: usare con cautela.

Queste azioni non sono un “ignore all” globale, ma permettono di rendere Codacy praticabile su progetti già maturi.

---

### 6) Setup consigliato (pragmatico) per repository con tanto legacy

Obiettivo: PR check rigorosi sul nuovo codice, senza bloccare lo sviluppo per il passato.

Configurazione tipica:
- **Quality Gate**:
  - soglia **0** su nuove issue **Major/Critical**;
  - soglia **0** su nuove issue **Security**;
  - soglie ragionevoli su duplicazione/complessità introdotte;
  - coverage: valutare in base alla maturità del progetto (spesso “diff coverage” è più sensata della coverage globale).
- **GitHub**:
  - rendere Codacy **required** solo dopo un periodo di taratura;
  - proteggere il branch principale con required checks.
- **Legacy management**:
  - evitare ignore indiscriminati;
  - usare ignore mirato solo per falsi positivi o codice non manutenuto;
  - pianificare refactoring incrementali (quando si tocca legacy, migliorare l’area).

Risultato atteso:
- PR checks “puliti” e utili;
- riduzione regressioni;
- qualità che migliora nel tempo, senza “big bang refactor”.

---

## Approfondimento tecnico: Code Patterns abilitati e disabilitati in MS3

Questa sezione integra il manuale con una lettura **critica e didattica** dei code patterns attivi e disattivati nel progetto MS3.  
L’obiettivo è fornire un quadro comprensibile a studenti e docenti con background informatico, collegando:
- **cosa fa ogni tool/pattern**,  
- **perché è utile**,  
- **perché è stato abilitato o disabilitato** nel contesto MS3.

> Nota: in Codacy il termine “Code Patterns” indica regole gestite da diversi strumenti (linters, analyzer e security scanner).  
> In questo approfondimento, per semplicità, consideriamo i “pattern” come **famiglie di regole** fornite da ciascun tool.

### 1) Code patterns **abilitati** (strumenti attivi)

#### Bandit (Python — security)
- **Cosa fa**: analizza codice Python alla ricerca di pattern di sicurezza noti (es. uso insicuro di `eval`, generazione casuale non sicura, gestione errata di credenziali).  
- **Perché serve**: intercetta vulnerabilità classiche prima che arrivino in produzione.  
- **Perché abilitato**: MS3 include script/utility e componenti Python; un controllo sicurezza è essenziale per ridurre il rischio di CWE comuni.

#### Checkstyle (Java — style/consistency)
- **Cosa fa**: verifica stile e convenzioni Java (naming, spaziature, import, struttura dei file).  
- **Perché serve**: migliora leggibilità e riduce variabilità nello stile di codice tra sviluppatori.  
- **Perché abilitato**: nel backend Java 11/Spring Boot, coerenza e manutenibilità sono prioritarie.

#### ESLint (JavaScript/TypeScript — linting)
- **Cosa fa**: rileva errori logici e violazioni di best practice in JS/TS (variabili inutilizzate, pattern rischiosi, stile).  
- **Perché serve**: evita bug tipici di JS e migliora qualità del frontend React.  
- **Perché abilitato**: il frontend MS3 è React; ESLint è lo standard de-facto per qualità JS.

#### Jackson Linter (JSON — formatting/validity)
- **Cosa fa**: controlla la validità e la formattazione di JSON secondo parsing Jackson.  
- **Perché serve**: riduce errori di configurazione e scambio dati (config, payload).  
- **Perché abilitato**: MS3 usa JSON in configurazioni e API; prevenire errori di parsing è critico.

#### Lizard (multi-language — complexity)
- **Cosa fa**: misura complessità ciclomatica e dimensioni delle funzioni.  
- **Perché serve**: identifica codice difficile da testare o manutenere.  
- **Perché abilitato**: aiuta a mantenere le modifiche entro soglie di complessità in PR.

#### markdownlint (Markdown — style)
- **Cosa fa**: verifica lo stile dei documenti Markdown (heading, spacing, link).  
- **Perché serve**: mantiene documentazione consistente e leggibile.  
- **Perché abilitato**: la documentazione è parte della DoD; qualità dei manuali è rilevante.

#### PMD (Java — bugs/maintainability)
- **Cosa fa**: analizza Java per bug potenziali e code smells (duplicazione, logica ridondante, API misuse).  
- **Perché serve**: riduce difetti prima del runtime e migliora qualità del codice.  
- **Perché abilitato**: integra Checkstyle con analisi più semantica su backend Java.

#### Prospector (Python — linting aggregator)
- **Cosa fa**: aggrega più strumenti Python (pylint, pep8/pycodestyle, mccabe, ecc.).  
- **Perché serve**: fornisce una vista unificata su qualità Python.  
- **Perché abilitato**: garantisce copertura ampia di regole senza strumenti manualmente separati.

#### Pylint (Python — linting)
- **Cosa fa**: analisi statica Python (errori, stile, complessità, design).  
- **Perché serve**: intercetta bug e migliora leggibilità.  
- **Perché abilitato**: standard consolidato, complementare a Bandit e Prospector.

#### PSScriptAnalyzer (PowerShell — linting/security)
- **Cosa fa**: controlla script PowerShell per best practice e rischi di sicurezza.  
- **Perché serve**: evita errori in script di automazione o tooling.  
- **Perché abilitato**: MS3 usa script di supporto (es. automazione CI, tooling locale).

#### Semgrep (multi-language — static analysis/security)
- **Cosa fa**: engine di pattern-matching su codice; rileva vulnerabilità e anti-pattern con regole custom.  
- **Perché serve**: copre casi non presi da altri tool ed è flessibile.  
- **Perché abilitato**: utile per sicurezza e per regole specifiche a un progetto.

#### ShellCheck (Shell — linting)
- **Cosa fa**: analizza script bash/sh per errori comuni e best practice.  
- **Perché serve**: evita bug sottili in script di build e deploy.  
- **Perché abilitato**: MS3 include script shell nel workflow di build e tooling.

#### SpotBugs (Java — bug finding)
- **Cosa fa**: rileva bug in Java basandosi su bytecode e pattern noti.  
- **Perché serve**: identifica difetti non evidenti dal solo stile (null dereference, concurrency, ecc.).  
- **Perché abilitato**: aumenta affidabilità del backend.

#### SQLint (SQL — linting)
- **Cosa fa**: analizza query SQL per errori sintattici e best practice.  
- **Perché serve**: riduce bug nei database script e query.  
- **Perché abilitato**: il progetto usa PostgreSQL e SQL è parte della pipeline.

#### Stylelint (CSS/SCSS — linting)
- **Cosa fa**: verifica stile e validità di CSS/SCSS.  
- **Perché serve**: evita regressioni visive e inconsistenze stilistiche.  
- **Perché abilitato**: il frontend React necessita di qualità nel layer di styling.

#### Trivy (security — dependency/container/IaC)
- **Cosa fa**: scanner per vulnerabilità su dipendenze, container e configurazioni.  
- **Perché serve**: identifica CVE e configurazioni insicure in fase CI.  
- **Perché abilitato**: aumenta sicurezza supply-chain e infrastrutturale.

#### TSQLLint (T-SQL — linting)
- **Cosa fa**: linting specifico per T-SQL (dialetto SQL Microsoft).  
- **Perché serve**: utile se sono presenti script SQL legacy o compatibilità con T-SQL.  
- **Perché abilitato**: garantisce qualità per eventuali script T-SQL presenti o di integrazione.

---

### 2) Code patterns **disabilitati** (strumenti non attivi)

#### Checkov (IaC — security)
- **Cosa fa**: scanner di configurazioni Infrastructure as Code (Terraform, CloudFormation, Kubernetes).  
- **Perché servirebbe**: rileva misconfigurazioni infrastrutturali e rischi di sicurezza.  
- **Perché disabilitato**: se MS3 non contiene IaC significativa o la copertura è gestita da altri strumenti, può essere rumore inutile.

#### ESLint9 (JavaScript — nuova major)
- **Cosa fa**: versione principale aggiornata di ESLint con nuove regole e breaking changes.  
- **Perché servirebbe**: miglioramenti e nuove regole per JS/TS.  
- **Perché disabilitato**: evitare breaking changes o divergenze con la toolchain esistente (config legacy, plugin non compatibili).

#### PMD7 (Java — nuova major)
- **Cosa fa**: nuova major di PMD con regole aggiornate.  
- **Perché servirebbe**: regole più moderne e accurate.  
- **Perché disabilitato**: possibili incompatibilità con rule set attuali o incremento di falsi positivi.

#### remark-lint (Markdown — linting alternativo)
- **Cosa fa**: linting Markdown tramite ecosistema remark.  
- **Perché servirebbe**: regole più estendibili e pipeline Markdown avanzata.  
- **Perché disabilitato**: già presente markdownlint; usare entrambi potrebbe creare duplicazioni e conflitti.

#### Ruff (Python — linting/performance)
- **Cosa fa**: linter Python ad alte prestazioni, unifica molte regole di tool diversi.  
- **Perché servirebbe**: velocità e copertura ampia.  
- **Perché disabilitato**: potrebbe sovrapporsi a Pylint/Prospector e alterare l’attuale baseline.

#### Spectral (API — linting OpenAPI)
- **Cosa fa**: linting di specifiche OpenAPI/AsyncAPI.  
- **Perché servirebbe**: garantisce qualità delle API contract.  
- **Perché disabilitato**: utile solo se la specifica API è mantenuta in repo e parte della DoD; se non presente è rumore.

#### SQLFluff (SQL — linting/formatting)
- **Cosa fa**: linter SQL avanzato con supporto multi-dialect e formattazione.  
- **Perché servirebbe**: maggiore controllo di stile e quality sulle query.  
- **Perché disabilitato**: già presente SQLint/TSQLLint; aggiungerlo potrebbe aumentare sovrapposizioni e falsi positivi.

---

### 3) Considerazioni didattiche e operative

#### Coerenza di copertura
- L’insieme di tool **abilitati** copre linguaggi e asset effettivamente presenti: Java, JS/React, Python, Shell, PowerShell, SQL, Markdown, CSS.  
- Questo assicura una **copertura trasversale** senza lasciare “zone d’ombra” nella codebase.

#### Evitare duplicazione di regole
- L’uso simultaneo di tool con scopi sovrapposti (es. due linter SQL o due linter Markdown) può generare **rumore** e conflitti.  
- La scelta MS3 privilegia un set **complementare**, con overlap limitato.

#### Motivazioni di sicurezza
- Tool come Bandit, Semgrep e Trivy coprono rispettivamente sicurezza su codice, pattern custom e supply-chain.  
- Questo approccio multilivello riduce il rischio che una vulnerabilità sfugga a un singolo analizzatore.

#### Allineamento alla Definition of Done (DoD)
- Gli strumenti abilitati supportano la DoD: prevenzione regressioni, standard di qualità, sicurezza e manutenibilità.  
- Le disabilitazioni sono mirate a evitare breaking changes e duplicazioni.

---

### 4) Sintesi per studenti e docenti

Nel progetto MS3 i code patterns **attivi** sono stati scelti per coprire in modo equilibrato:
- **Qualità del codice** (style, complessità, bug finding),
- **Sicurezza** (pattern insicuri, vulnerabilità, dipendenze),
- **Documentazione e configurazioni** (Markdown, JSON).

I pattern **disabilitati** non sono “peggiori”, ma:
- potrebbero duplicare strumenti già presenti,
- introdurre breaking changes,
- oppure non essere rilevanti per gli artefatti presenti nel repository.

Questa selezione mira a **massimizzare il valore didattico e operativo** delle analisi, mantenendo le segnalazioni utili e gestibili.
