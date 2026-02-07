# Story 3

## Microtask 3.1

### **Livello 1: Business Goal (Organizzativo)**

**Task 1: Definizione Business Goal**

**Goal:** Ridurre il rischio di burnout e aumentare il tasso di retention del personale medico in MS3, migliorando la qualità della vita lavorativa percepita.

* **Activity:** Ridurre (il burnout) / Aumentare (la retention)  
* **Focus:** Burnout del personale e Retention (Benessere lavorativo)  
* **Object:** Dottori  
* **Magnitude:** 10% (indice benessere)  
* **Timeframe:** 26/01/26 to 26/02/26   
* **Scope:** CSW-MS3 (corso universitario)  
* **Constraints:** Costi implementativi azzerati, vincoli contrattuali nazionali sui turni  
* **Relation with other goals:** È il goal padre che genera il *Software Goal* (Livello 2\) relativo al gradimento delle schedulazioni.

**Task 2: Strategia Scelta (Livello 1\)**

Cambiamento della pianificazione per la gestione delle risorse umane (Passaggio da pianificazione rigida a pianificazione flessibile/intelligente).

**Task 3: Measurement Plan (Livello 1\)**

### **Questions & Metrics**

#### **Q1 (Retention & Stability): Il personale manifesta l'intenzione di rimanere nel reparto?**

*Vogliamo capire se il miglioramento dei turni riduce la voglia di fuggire dal reparto.*

* **M1.1 \- Tasso di Richieste di Trasferimento (TRT):**  
  * Numero di richieste formali o informali di trasferimento ad altri reparti/ospedali presentate durante il periodo.  
  * *Confronto:* Rispetto alla media dei 3 mesi precedenti.  
* **M1.2 \- Turnover Rate Mensile (TR):**  
  * Percentuale di staff che ha lasciato effettivamente l'incarico.  
  * *(Nota: Su un periodo breve di 1 mese, questo dato potrebbe essere poco significativo, ma è vitale nel lungo periodo).*

#### **Q2 (Burnout & Health): C'è una riduzione dei sintomi oggettivi di stress?**

*Vogliamo misurare i "costi occulti" della cattiva pianificazione.*

* **M1.3 \- Tasso di Assenteismo per Stress (TAS):**  
  * Numero di giorni di malattia "brevi" (1-3 giorni) o legati esplicitamente a diagnosi da stress/fatica.  
  * *Ipotesi:* Turni migliori $\\rightarrow$ Meno malattie "tattiche" o da esaurimento.  
* **M1.4 \- Numero di Reclami sui Turni (NRT):**  
  * Conteggio delle lamentele formali (email a HR, segnalazioni sindacali) riguardanti la turnistica.  
  * *Target:* Riduzione del 50% rispetto allo storico.

#### **Q3 (Perception): Migliora il clima aziendale complessivo?**

*Questa metrica correla direttamente con il "Gradimento" misurato al Livello 2\.*

* **M1.5 \- eNPS (Employee Net Promoter Score) specifico:**  
  * Domanda sondaggio: *"Su una scala da 0 a 10, quanto consiglieresti questo reparto a un collega basandoti sull'attuale gestione dei turni?"*  
  * Formula: $\\% \\text{Promoters} (9-10) \- \\% \\text{Detractors} (0-6)$.  
* **M1.6 \- Indice di Work-Life Balance (WLB):**  
  * Media delle risposte alla domanda: *"Quanto ritieni che i tuoi turni rispettino la tua vita privata questo mese?"* (Scala 1-5).

### 

### **Livello 2: Software Goal (Migliorare Gradimento)**

**Task 1: Definizione Software Goal**

Goal: aumentare il livello di gradimento delle schedulazioni generate dal sistema di MS3, basandosi sui feedback lasciati dal personale relativamente ai vari turni a loro assegnati.

* **activity**: aumentare    
* **focus**: gradimento da parte dei dottori  
* **object**: sistema di schedulazione  
* **magnitude**: 10%  
* **timeframe**: 26/01/26 to 26/02/26  
* **scope**: CSW-MS3 (corso universitario)  
* **constraints**: costi implementativi azzerati  
* **relation with other goals**: Nessuna, essendo l’unico specializzato. Non ci sono goal complementari

**Task 2: Strategia Scelta (Livello 2\)**

Introduzione di agenti AI per migliorare le schedulazione esistenti.

**Task 3: Measurement Plan (Livello 2\)**

#### **Q1 (Caratterizzazione): Qual è l'attuale livello di gradimento degli scheduling da parte dei dottori?**

*Queste metriche definiscono il livello di gradimento di una schedulazione.*

* **M2.1** \- differenza degli UP rispetto alla schedulazione immediatamente precedente (delta)  
* **M2.2** \- tipo, media e varianza dei feedback ricevuti  
* **M2.3** \- numero di UP assegnati alla schedulazione meno il minimo assegnabile, diviso la differenza tra il massimo e il minimo assegnabile ($UP\_N\\in(0,1)$)

#### **Q2 (Valutazione): Quanto è migliorato il gradimento?**

*Queste metriche consentono di caratterizzare la variazione, dal punto di vista del gradimento da parte dei dottori, tra una schedulazione “classica” e una “migliorata”.*

* **M2.4** \- differenza tra il valore del $UP\_N$ della nuova schedulazione con il valore di quella originale ($\\Delta\_{UP\_N}$)  
* **M2.5** \- differenza tra la varianza degli UP dei dottori rispetto alla schedulazione originale ($\\Delta\_{\\sigma^2}$)  
* **M2.6** \- media delle differenze tra il valore degli UP dei singoli dottori rispetto alla schedulazione precedente ($\\mu\_\\Delta$)  
* **M2.7** \- varianza delle differenze tra il valore degli UP dei singoli dottori rispetto alla schedulazione precedente ($\\sigma^2\_\\Delta$)  
* **M2.8** \- minimo delle differenze tra il valore degli UP dei singoli dottori rispetto alla schedulazione precedente ($\\Delta\_{min}$)  
* **M2.9** \- massimo delle differenze tra il valore degli UP dei singoli dottori rispetto alla schedulazione precedente ($\\Delta\_{max}$)  
* **M2.10** \- numero di cambiamenti di temperatura da negativo a neutro (-N) nei feedback rispetto alla schedulazione precedente  
* **M2.11** \- numero di cambiamenti di temperatura da negativo a positivo (-+) nei feedback rispetto alla schedulazione precedente  
* **M2.12** \- numero di cambiamenti di temperatura da neutro a positivo (N+) nei feedback rispetto alla schedulazione precedente  
* **M2.13** \- numero di cambiamenti di temperatura da neutro a negativo (N-) nei feedback rispetto alla schedulazione precedente  
* **M2.14** \- numero di cambiamenti di temperatura da positivo a negativo (+-) nei feedback rispetto alla schedulazione precedente  
* **M2.15** \- numero di cambiamenti di temperatura da positivo a neutro (+N) nei feedback rispetto alla schedulazione precedente

#### **Q4 (Valutazione): La schedulazione è migliorata in maniera significativa?**

*Queste metriche forniscono un modello interpretativo delle metriche calcolate nel punto precedente.*

* **M2.16** \- $\\Delta\_{UP\_N}\\leq 0$  
* **M2.17** \- $\\Delta\_{\\sigma^2}\\leq 0$  
* **M2.18** \- $CV\_\\Delta=\\left|\\frac{\\sigma\_\\Delta}{\\mu\_\\Delta}\\right|\\leq 1$  
* **M2.19** \- $\\Delta\_{max} \\leq 0$  
* **M2.20** \- $-N\\geq0$  
* **M2.21** \- $-+\\geq 0$  
* **M2.22** \- $N+\\geq 0$  
* **M2.23** \- $N-\\leq 0$  
* **M2.24** \- $+-\\leq 0$  
* **M2.25** \- $+N\\leq 0$

### **Livello 3: Operational Goal (Tecnico/Processo)**

**Task 1: Definizione Operational Goal**

**Goal:** Integrazione di un sistema di IA che permetta la modifica automatica delle schedulazioni esistenti a seconda dei feedback ricevuti.

* **Activity:** Integrazione  
* **Focus:** Sistema di IA  
* **Object:** Sistema di schedulazione  
* **Magnitude:** Generazione corretta di 3 scenari validi per ogni ciclo di pianificazione.  
* **Timeframe:** 26/01/26 to 26/02/26  
* **Scope:** Modulo Backend del sistema MS3  
* **Constraints:**  
  * Nessuna interazione diretta con i dottori (Scenario Planner-Only).  
  * Rispetto dei limiti tecnici delle API dei modelli LLM (Rate Limits).  
  * Nessun costo per le chiamate API dei modelli LLM.  
* **Relation with other goals:** Supporta il *Software Goal (Livello 2\)* fornendo le schedulazioni necessarie a migliorare il gradimento.

**Task 2: Strategia Scelta (Livello 3\)**

Attraverso un’ interpretazione semantica dei feedback storici (analisi preliminare dei testi), definiamo due possibili strategie:

#### **Activity 1: Strategia "Direct Prompting" (On-Demand UI Trigger)**

* **Descrizione Operativa:** Lo schedulatore preme il pulsante "Genera Schedulazione" sulla dashboard.  
* **Meccanismo Tecnico:**  
  * Il sistema raccoglie istantaneamente tutti i feedback storici e i vincoli attuali dal database.  
  * Questi dati vengono "impacchettati" in un unico, grande contesto (System Prompt) inviato all'LLM.  
  * L'LLM elabora il contesto in una singola sessione di inferenza e restituisce le 3 schedulazioni richieste.

#### **Activity 2: Strategia "MCP Integration" (Model Context Protocol)**

* **Descrizione Operativa:** Il processo di generazione utilizza un **Server MCP** dedicato.  
* **Meccanismo Tecnico:**  
  * Quando viene richiesta la generazione, l'Agente AI non riceve tutti i dati in un blocco di testo, ma utilizza "Tools" specifici definiti via MCP (es. get\_doctor\_feedback, check\_constraints).  
  * L'AI "interroga" il database in modo iterativo per recuperare solo i contesti necessari e valida i vincoli semantici attraverso il protocollo standard.  
  * L'output viene costruito in modo modulare garantendo che i dati non vengano allucinati.

### 

Entrambe le strategie dovranno presentare in output un *Multi-Scenario Generation,* ovvero tre schedulazioni distinte (Empatica, Efficiente, Bilanciata) da sottoporre al Pianificatore. 

#### **1\. Schedulazione Empatica (Doctor-Oriented)**

* **Priorità:** Massimizzare il benessere psicologico e ridurre il rischio di lamentele future.  
* **Logica:**  
  * L'interpretazione semantica dei feedback (es. "Sono stressato dai turni notturni") viene convertita in **Hard Constraints** (Vincoli Rigidi).  
  * L'algoritmo **rifiuta** qualsiasi assegnazione che violi una preferenza espressa esplicitamente o implicitamente nel testo.  
* **Trade-off:**  
  * Il "costo" in Uffa Points sarà probabilmente più alto (meno efficiente).  
  * Potrebbe lasciare alcuni turni scoperti se nessuno vuole farli, richiedendo l'intervento del pianificatore per forzare la mano o chiamare esterni.

#### **2\. Schedulazione Efficiente (Organization-Oriented)**

* **Priorità:** Ottimizzare i metriche quantitative dell'ospedale (Equità matematica e Copertura totale).  
* **Logica:**  
  * Ignora i vincoli semantici soggettivi ("Soft Constraints").  
  * Utilizza un algoritmo di ottimizzazione pura per minimizzare la somma totale degli Uffa Points e la Varianza.  
* **Trade-off:**  
  * Garantisce la copertura perfetta e l'equità numerica.  
  * Alto rischio di scontentare i medici che avevano segnalato problemi personali, portando potenzialmente a un aumento dei feedback negativi nel mese successivo.

#### **3\. Schedulazione Bilanciata (The AI Recommendation)**

* **Priorità:** Il compromesso sostenibile (Pareto-Optimal).  
* **Logica:**  
  * Tratta i feedback semantici come **Soft Constraints** con penalità elevate (ma non infinite).  
  * L'AI cerca di evitare i turni sgraditi, ma se l'alternativa è sovraccaricare ingiustamente un collega o lasciare un buco critico, procede all'assegnazione applicando una logica di "Minimo Danno".  
* **Trade-off:**  
  * Non è perfetta né matematicamente né emotivamente, ma è la soluzione che massimizza la *retention* a lungo termine mantenendo il servizio attivo.

**Task 3: Measurement Plan (GQM Livello 3\)**

Al fine di validare la scelta architetturale, le metriche definite nel GQM del Software Goal vengono utilizzate per confrontare le schedulazione prodotte dalle Activity 1 e 2\.

**Metrica di Confronto (Delta Strategy):**

Per ogni metrica $M$ del GQM originale (dove $M \\in \\{\\Delta\_{UP\_N}, \\Delta\_{\\sigma^2}, \-N, \-+\\}$), calcoliamo il differenziale di performance:

$$\\Delta\_{Perf} \= M(\\text{Activity}\_2) \- M(\\text{Activity}\_1)$$

**Interpretazione:**

1. **Metriche "Lower is Better"** (es. $\\Delta\_{UP\_N}, \\Delta\_{\\sigma^2}, \-N$):  
   * Se $\\Delta\_{Perf} \< 0$, l'Activity 2 (MCP) è superiore.  
   * Se $\\Delta\_{Perf} \> 0$, l'Activity 1 (Prompting) è superiore.  
2. **Metriche "Higher is Better"** (es. $-+, N+$):  
   * Se $\\Delta\_{Perf} \> 0$, l'Activity 2 (MCP) è superiore.  
   * Se $\\Delta\_{Perf} \< 0$, l'Activity 1 (Prompting) è superiore.

Questo approccio permette di stabilire scientificamente se la maggiore complessità implementativa dell'architettura MCP (Activity 2\) è giustificata da un miglioramento tangibile nella qualità delle schedulazioni prodotte rispetto al Direct Prompting (Activity 1).

## Microtask 3.2

# **Artifact: Data-to-Metric Matrix**

This document maps the metrics defined in the GQM+S plan to their potential data sources within the CSW-MS3 project.

## **Level 1: Business Goal (Organizzativo)**

| Metric (ID & Name) | Data Required | Potential Data Source(s) | Notes & Actions Required |
| :---- | :---- | :---- | :---- |
| **M1.1 \- Tasso di Richieste di Trasferimento (TRT)** | Log of formal/informal transfer requests. | UserAPI.js, **Proposed: TransferRequestAPI**. | **Action:** New system needed to log transfer requests. |
| **M1.2 \- Turnover Rate Mensile (TR)** | List of doctors leaving per month. | UserAPI.js, DoctorAPI.js. | **Verify:** Backend must track employment\_end\_date. |
| **M1.3 \- Tasso di Assenteismo per Stress (TAS)** | Log of sick days (short-term/stress). | HolidaysAPI.js. **Proposed: AbsenceAPI**. | **Action:** Extend system to allow logging sick leave with reasons. |
| **M1.4 \- Numero di Reclami sui Turni (NRT)** | Count of formal complaints. | ScheduleFeedbackAPI.js. | **Action:** Enhance feedback system with "Formal Complaint" category. |
| **M1.5 \- eNPS** | Survey responses (0-10 scale). | **Proposed: SurveyAPI**. | **Action:** Build survey module (Backend \+ Frontend). |
| **M1.6 \- Work-Life Balance (WLB)** | Survey responses (1-5 scale). | **Proposed: SurveyAPI**. | **Action:** Implement within the eNPS survey module. |

## 

## **Level 2: Software Goal (Migliorare Gradimento)**

## **Level 2: Software Goal (Migliorare Gradimento)**

| Metric (ID & Name) | Data Required | Potential Data Source(s) | Notes & Actions Required |
| :---- | :---- | :---- | :---- |
| **M2.1 - Delta UP** | Uffa Points (current vs previous). | `ScheduleAPI.js`, `Doctor.js`. | **Action:** Backend service to calculate/store historical UP snapshots. |
| **M2.2 - Sentiment Feedback** | Categorized feedback (pos/neu/neg). | `ScheduleFeedbackAPI.js`. | **Action:** Implement Sentiment Analysis process on raw text. |
| **M2.3 - $UP_N$ (Normalizzato)** | UP, Min/Max assignable values. | `ScheduleAPI.js`, `VincoliAPI.js`. | **Action:** Service to compute normalized score based on constraints. |
| **M2.4, M2.5 - Global Deltas ($\Delta_{UP_N}, \Delta_{\sigma^2}$)** | Aggregated UP stats from old/new schedules. | `ScheduleAPI.js`, `ScheduleSnapshot`. | **Action:** The AI Scheduler must output these global deltas in its response. |
| **M2.6 - M2.9 - Individual Deltas ($\mu_\Delta, \sigma^2_\Delta, \Delta_{min/max}$)** | Per-doctor UP values for current ($t$) and previous ($t_{-1}$) schedule. | `ScheduleSnapshot` (JSON blob of previous UPs). | **Action:** Need to parse the previous schedule's snapshot to compare doctor-by-doctor. |
| **M2.10 - M2.15 - Sentiment Transitions (Temperature)** | Paired feedback status ($S_{t-1}, S_t$) for each doctor. | `ScheduleFeedbackAPI.js`. | **Action:** Service must query feedback by `doctor_id` for the last 2 schedules to detect state changes (e.g., Neg->Pos). |
| **M2.16 - M2.25 - Evaluation Thresholds** | Boolean results based on metrics M2.4 - M2.15. | **Calculated within AI Service**. | **Action:** These are logical checks performed by the AI or the Benchmarking Service to validate improvement. |

## 

## **Level 3: Operational Goal (AI Performance & Benchmarking)**

*Questa sezione mappa le metriche tecniche definite per l'Agente AI (Story 2).*

| Metric (ID & Name) | Data Required | Potential Data Source(s) | Notes & Actions Required |
| :---- | :---- | :---- | :---- |
| **M3.1 \- Optimality Score** | Score 0-1 generated by AI reasoning. | **AI JSON Response** (metadata.optimality\_score). | **Action:** Store this score in ScheduleGenerationLog table for trend analysis. |
| **M3.2 \- Uncovered Shifts Count** | Count of shifts left empty. | **AI JSON Response** (uncovered\_shifts array size). | **Action:** Parse JSON response and flag alerts if count \> 0\. |
| **M3.3 \- Soft Constraints Violated** | Count of forced assignments. | **AI JSON Response** (metrics.soft\_violations\_count). | **Action:** Compare this metric between *Direct Prompting* and *MCP* strategies. |
| **M3.4 \- Uffa Balance Improvement** | Pre vs Post variance of Uffa Points. | **AI JSON Response** (metrics.uffa\_balance). | **Action:** Persist this metric to validate AI effectiveness over manual scheduling. |
| **M3.5 \- Computation Time** | Time taken to generate schedule. | AIService.java (System logs). | **Action:** Measure latency to ensure API timeout compliance. |

## Microtask 3.3

**Implementation Summary (Metric Aggregation + Normalization Utilities)**

- Implementate utility pure per aggregare le metriche di gradimento e calcolare i delta per-doctor (media, varianza popolazione, min/max, coefficiente di variazione) e le transizioni di sentiment in accordo con le definizioni M2.6–M2.15 e M2.18.  
- Introdotte funzioni di normalizzazione/scaling per $UP_N$ e $\\Delta_{UP_N}$ basate sulla formula $(UP - Min) / (Max - Min)$.  
- Scelte chiave: validazione esplicita di input null/empty, bound non validi, mismatch di serie e valori sentiment fuori range con `IllegalArgumentException`, per evitare calcoli ambigui o non deterministici.  
- Classi create:  
  - `MetricNormalizationUtils` (normalizzazione $UP_N$ e delta normalizzato).  
  - `MetricAggregationUtils` (statistiche aggregate, delta per-doctor, conteggio transizioni).  
  - `UffaDeltaStats` (contenitore dei risultati aggregati).  
  - `SentimentTransitionCounts` (contenitore dei conteggi delle transizioni).  

## Microtask 3.4 — Multidimensional Priority Scale Config (defaults + overrides)

### Overview
Questa microtask introduce una **configurazione multidimensionale della priority scale** per il confronto fra schedulazioni AI. L’obiettivo è rendere esplicito e configurabile il peso assegnato a ciascuna dimensione/metrica di decisione, garantendo **default sensati**, **override parziali** e **validazione rigorosa** a runtime. La configurazione viene risolta in un’unica mappa `PriorityDimension -> weight` (somma = 1.0), pronta per essere usata dall’algoritmo di decisione (microtask 3.5).

### Config model
La configurazione è modellata con `@ConfigurationProperties` Spring Boot:

- **`PriorityScaleProperties`**
  - `defaults`: mappa `String -> Double` con i pesi di default.
  - `overrides`: mappa `String -> Double` con i pesi di override (parziali).

Le chiavi vengono mappate all’enum `PriorityDimension` che definisce esplicitamente le dimensioni attese:
`COVERAGE`, `UFFA_BALANCE`, `SENTIMENT_TRANSITIONS`, `UP_DELTA`, `VARIANCE_DELTA`.

### Defaults & Overrides
I default sono definiti in `application.properties` con prefisso:

```
ai.rescheduling.priority-scale.defaults.*
```

Gli override possono essere definiti con prefisso:

```
ai.rescheduling.priority-scale.overrides.*
```

La logica è **merge con defaults**: se un override è presente, sostituisce il valore della dimensione specificata, lasciando invariati gli altri pesi. Il risultato è una mappa completa e deterministica.

### Validation rules
La validazione avviene **ad ogni accesso** (runtime) tramite `PriorityScaleConfig`:

1. **Default obbligatori**: la sezione `defaults` deve esistere ed essere non vuota.
2. **Chiavi valide**: ogni chiave deve corrispondere a un valore dell’enum `PriorityDimension`.
3. **Pesi non-negativi**: i pesi devono essere ≥ 0.
4. **Copertura completa**: dopo il merge, tutte le dimensioni devono essere presenti.
5. **Somma = 1.0**: la somma dei pesi deve essere 1 (tolleranza numerica minima).

In caso di violazione viene lanciata `PriorityScaleValidationException`, in modo coerente con la strategia “validate on access”.

### Integration points
- **Layer di configurazione Spring**: `PriorityScaleProperties` è registrata via `@EnableConfigurationProperties` in `AppConfig`.
- **Uso previsto**: il servizio di decisione (microtask 3.5) dovrà invocare `PriorityScaleConfig#getPriorityScale()` per ottenere la mappa validata.
- **Override ambientali**: i valori possono essere sovrascritti tramite proprietà Spring (env vars, profile, etc.).

### Key classes/components
- `PriorityDimension` — enum delle dimensioni supportate.
- `PriorityScaleProperties` — schema di configurazione (`defaults` + `overrides`).
- `PriorityScaleConfig` — merge + validazione, restituisce la mappa immutabile dei pesi.
- `PriorityScaleValidationException` — errore runtime per configurazioni invalide.

### Testing
Test unitari (`PriorityScaleConfigTest`) coprono:
- merge di override parziali,
- dimensione mancante,
- somma pesi non valida,
- dimensione sconosciuta,
- pesi negativi.

### How to extend
Per aggiungere nuove dimensioni:
1. Estendere l’enum `PriorityDimension`.
2. Aggiornare i default in `application.properties`.
3. Aggiornare la documentazione/decision service per integrare la nuova dimensione.

Le regole di validazione garantiscono che nessuna dimensione resti non pesata.



## Microtask 3.5 — Decision Algorithm Service (Priority Scale–Driven)

### Overview
Questa microtask implementa il **decision algorithm service** che seleziona la schedulazione preferita tra più candidate, usando la **multidimensional priority scale** introdotta nella 3.4. L’algoritmo assume che tutte le metriche in input siano **normalizzate in [0,1] e “higher is better”**, così da consentire una combinazione lineare coerente con i pesi configurati.

### Decision logic & tie-breaks
- **Scoring principale:** *weighted sum* dei valori normalizzati per ogni `PriorityDimension`, usando la mappa validata da `PriorityScaleConfig#getPriorityScale()`.
- **Determinismo:** a parità di input, il risultato è deterministico; l’ordinamento dipende solo dai valori metrici e dai pesi.
- **Tie-break lexicografico (ordine fisso):**  
  `COVERAGE → UFFA_BALANCE → SENTIMENT_TRANSITIONS → UP_DELTA → VARIANCE_DELTA`.  
  Se il punteggio complessivo è equivalente (entro tolleranza numerica), si confrontano in sequenza le dimensioni sopra in ordine decrescente di priorità.
- **Input validation:** nessuna lista vuota/null, nessun valore metrica null/NaN/∞, e range **[0,1]** per ciascuna dimensione. Gli errori generano `IllegalArgumentException`, coerentemente con le utility metriche della 3.3.

### Classi introdotte/aggiornate
- **`AiScheduleCandidateMetrics`**: DTO “pure” che rappresenta una candidate schedule con i valori normalizzati delle dimensioni (coverage, uffa balance, sentiment transitions, UP delta, variance delta) e un `candidateId` stabile per debug/test.
- **`DecisionAlgorithmService`**: interfaccia di servizio per la selezione della schedulazione preferita.
- **`DecisionAlgorithmServiceImpl`**: implementazione concreta che:
  - recupera i pesi da `PriorityScaleConfig`,
  - calcola il *weighted sum*,
  - applica il tie-break lexicografico,
  - valida i dati in input.

### Integrazione con Priority Scale (Microtask 3.4)
Il servizio dipende direttamente da `PriorityScaleConfig` per ottenere la **mappa pesi validata** (default + override). In questo modo:
- tutte le dimensioni sono sempre presenti,
- la somma dei pesi è garantita pari a 1,
- la decisione è **configuration-driven** e pronta per override via proprietà Spring.

### Unit test strategy
I test unitari coprono:
- selezione corretta del candidato migliore con *weighted sum*,
- comportamento con **override** della priority scale,
- tie-break deterministico con ordine fisso delle dimensioni,
- validazione input (lista vuota, metrica fuori range),
- scenario realistico con tre schedulazioni tipiche (standard/empatica/efficiente).
  

## Microtask 3.6 — Comparison Payload Builder (DTO + Mapper)

### Obiettivo
Definire il **payload di confronto** per l’endpoint `/comparison`, includendo:
- dati grezzi della schedulazione (testo TOON o raw schedule text),
- metriche decisionali **raw** e **normalizzate**,
- metadati del candidato (scheduleId e/o candidateId + tipo),
- outcome della decisione (schedulazione selezionata).

### Scelte progettuali
- **Separazione domain vs DTO**: i modelli di dominio descrivono i dati necessari al confronto, mentre i DTO sono ottimizzati per la serializzazione JSON e l’uso nel layer REST.
- **CandidateId condizionale**: il campo `candidateId` viene esposto solo se `scheduleId` non è disponibile (es. prima della persistenza), per evitare duplicazione di identificativi e mantenere compatibilità con la UI.
- **Metriche duali**: ogni candidato include sia i valori **raw** (per trasparenza diagnostica) sia quelli **normalizzati** (per il punteggio decisionale), in coerenza con l’algoritmo di decisione di microtask 3.5.
- **Tipizzazione esplicita dei candidati**: i tipi `standard`, `empathetic`, `efficient`, `balanced` sono modellati con un enum, per garantire consistenza di labeling tra backend e frontend.

### Classi create (livello alto)
**Domain (`org.cswteams.ms3.ai.comparison.domain`)**
- `ScheduleCandidateType`: enum dei 4 tipi di schedulazione confrontati, con label stabile per l’output.
- `DecisionMetricValues`: contenitore delle metriche decisionali raw (coverage, uffa balance, sentiment transitions, UP delta, variance delta).
- `AiScheduleComparisonCandidate`: rappresenta un candidato con metadati, raw schedule text e metriche (raw + normalizzate).
- `AiScheduleDecisionOutcome`: rappresenta la scelta finale (candidateId/scheduleId + tipo).

**DTO (`org.cswteams.ms3.ai.comparison.dto`)**
- `AiScheduleCandidateMetadataDto`: metadati serializzati del candidato (candidateId opzionale, scheduleId, type).
- `AiScheduleDecisionMetricValuesDto`: struttura standard per set di metriche (raw o normalizzate).
- `AiScheduleDecisionMetricsDto`: wrapper che espone `raw` e `normalized`.
- `AiScheduleComparisonCandidateDto`: payload del candidato in output (metadata + rawScheduleText + metrics).
- `AiScheduleDecisionOutcomeDto`: outcome della decisione, esposto tramite metadata del candidato selezionato.
- `AiScheduleComparisonResponseDto`: risposta complessiva con lista candidati + decisionOutcome.

**Mapper (`org.cswteams.ms3.ai.comparison.mapper`)**
- `AiScheduleComparisonMapper`: converte il dominio in DTO, gestisce null-safety, filtra candidati null e applica la regola di esposizione `candidateId` solo se `scheduleId` è assente.

### Risultato
- Il backend dispone di un modello strutturato e serializzabile per il confronto di 4 schedulazioni, pronto per il consumo UI.
- Le metriche sono disponibili in doppia forma (raw + normalized) per garantire sia **spiegabilità** sia **compatibilità** con l’algoritmo di decisione.
- L’output è coerente con le convenzioni JSON già presenti (naming consistente con DTO esistenti).

## Microtask 3.7

- Il parser `AiScheduleJsonParser` espone il fail-on-unknown-properties via costruttore (strict configurabile). Se abilitato, proprietà sconosciute causano `SCHEMA_MISMATCH` con categoria `APPLICATION_SCHEMA`.
- È stato aggiunto il flag `failOnTypeMismatch`: se attivo, mismatch di tipo (array/object, string/number, ecc.) generano `TYPE_MISMATCH` con path nel messaggio (es. `$.assignments[0].doctor_id`). La modalità permissiva è supportata tramite coercizioni scalari.
- La mappatura dei DTO riflette 1:1 lo schema JSON del microtask 2.3 con naming camelCase in Java e `@JsonProperty` per snake_case. I DTO risiedono in `org.cswteams.ms3.ai.protocol.dto` e includono root, metadata/metrics, assignments, uncovered_shifts e uffa_delta.
- Le collezioni nella root sono inizializzate a liste vuote per robustezza. Sono presenti enum di protocollo (`AiStatus`, `AiUffaQueue`) e `role_covered` è mappato su `Seniority`.
- È stata introdotta una validazione semantica separata dal parsing (`AiScheduleSemanticValidator`) con errori strutturati path-based: campi obbligatori, range numerici, vincoli per assignments, assenza di duplicati su coppia (shift_id, doctor_id).
- Allineamento ADR: `shift_id` è validato come exchange-id nel formato `S_<id>_<yyyyMMdd>` (con parsing data), senza lookup DB; `role_covered` è limitato a STRUCTURED/JUNIOR (mappato su `Seniority.SPECIALIST_JUNIOR` per compatibilità dominio).
- Test unitari coprono: JSON malformato, proprietà sconosciute, mismatch di tipo, enum non validi, mapping completo dei DTO, validazione semantica (missing/range/duplicate), enforcement status (SUCCESS ok, PARTIAL_SUCCESS/FAILURE errore) e allineamento ADR su `shift_id`.

**Classi audit-related (modulo Audit Validation):**
- `ErrorCategory`, `ValidationViolation`, `MetricValidationException`: tassonomia errori e dettagli strutturati (path/message) con supporto correlationId (da MDC `requestId`).
- `MetricComputationResult` + `MetricComputationValidator`: validazione QA su metriche computate (missing/NaN/Infinity/out-of-range) e copertura audit; errori con codice stabile e dettagli in `violations`.
- `MetricValidationErrorResponse` + `MetricValidationExceptionHandler`: risposta HTTP standardizzata (`status=FAILURE`) per errori di validazione.
- `AuditSelection` + `AuditSelectionAspect`: AOP per generare eventi audit sui metodi di selezione/ranking.
- `SelectionAuditEvent`, `AuditableSelectionResult`, `AuditedSelectionResult`: modello eventi e wrapper per esportare eventi audit dal risultato di selezione.
- `AuditRecorder`: logger dedicato `MS3_AUDIT`, arricchisce con tenantId (da `TenantContext`, fallback `central_db`) e correlationId.
