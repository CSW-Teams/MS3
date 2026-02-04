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

# **Artifact: Data-to-Metric Mapping Matrix**

**Descrizione:** Questo documento mappa ogni variabile delle formule GQM direttamente sugli attributi delle entità del sistema MS3 (esistenti o da creare).

## **Legenda Mappatura**

* **\[E\] Existing:** Il campo esiste già nel dominio.  
* **\[N\] New:** Il campo o l'entità deve essere creato (Story 4/5).  
* **\[C\] Calculated:** Valore derivato a runtime.

---

## **1\. Livello 1: Business Metrics (HR & Salute)**

| ID | Variabile Formula | Entità Sorgente | Campo / Attributo | Logica di Estrazione / Filtro |
| :---- | :---- | :---- | :---- | :---- |
| **M1.1** | **Richieste Trasf.** | TransferRequest **\[N\]** | creation\_date | COUNT(id) WHERE date in current month AND type \= 'TRANSFER' |
| **M1.2** | **Staff Uscito** | User **\[E\]** | system\_actors, status **\[N\]** | COUNT(id) WHERE role\='DOCTOR' AND status changed to 'TERMINATED' in month |
| **M1.3** | **Giorni Malattia** | Holiday **\[E\]** | category **\[N\]**, start\_date, end\_date | SUM(days) WHERE category\='SICKNESS\_STRESS' AND date in current month |
|  | *Giorni Lavorabili* | ConcreteShift **\[E\]** | date, doctors\_allocated | COUNT(distinct date) \* COUNT(active\_doctors) |
| **M1.4** | **Reclami** | Feedback **\[N\]** | category **\[N\]**, creation\_date | COUNT(id) WHERE category\='FORMAL\_COMPLAINT' |
| **M1.5** | **Promoters (9-10)** | SurveyResponse **\[N\]** | question\_id, score | COUNT(id) WHERE question\_type\='eNPS' AND score \>= 9 |
|  | *Detractors (0-6)* | SurveyResponse **\[N\]** | question\_id, score | COUNT(id) WHERE question\_type\='eNPS' AND score \<= 6 |

---

## **2\. Livello 2: Software Metrics (Gradimento & UP)**

Qui mappiamo le metriche sugli oggetti Java analizzati nella Baseline (DoctorUffaPriority, ConcreteShift).

| ID | Variabile Formula | Entità Sorgente | Campo / Attributo | Logica di Estrazione |
| :---- | :---- | :---- | :---- | :---- |
| **M2.1** | $UP\_{current}$ | DoctorUffaPriority **\[E\]** | general\_priority, night\_priority | Valore attuale letto a runtime. |
|  | $UP\_{prev}$ | ScheduleSnapshot **\[N\]** | priority\_snapshot\_json | Valore salvato al momento della generazione precedente. |
| **M2.2** | **Sentiment** | ScheduleFeedback **\[E\]** | sentiment\_score **\[N\]** | Analisi NLP sul campo message (Text). Valore: \-1 (Neg), 0 (Neu), 1 (Pos). |
| **M2.3** | $UP\_{min/max}$ | PriorityConfig **\[E\]** | min\_bound, max\_bound | Letti da priority.properties (Baseline). |
| **M2.4** | $\\Delta\_{UP\_N}$ | **\[C\]** | *Vedi M2.1* | Calcolato: $(UP\_{curr} \- UP\_{prev})$ normalizzato su $(Max \- Min)$. |
| **M2.5** | $\\sigma^2$ (Varianza) | **\[C\]** | *Vedi M2.1* | VAR\_POP su tutti i valori general\_priority dei medici attivi. |
| **M2.6** | $\mu_{\Delta}$ (Media Diff) | **[C]** | *Vedi M2.1* | `AVG(UP_curr - UP_prev)` per tutti i medici attivi. |
| **M2.7** | $\sigma^2_{\Delta}$ (Var Diff) | **[C]** | *Vedi M2.1* | `VAR_POP(UP_curr - UP_prev)` per tutti i medici attivi. |
| **M2.8** | $\Delta_{min}$ | **[C]** | *Vedi M2.1* | `MIN(UP_curr - UP_prev)`. |
| **M2.9** | $\Delta_{max}$ | **[C]** | *Vedi M2.1* | `MAX(UP_curr - UP_prev)`. |
| **M2.10** | **Temp. Change (-N)** | ScheduleFeedback **\[E\]** | sentiment\_score | COUNT WHERE $S\_{t-1} \= \-1$ AND $S\_{t} \= 0$. |
| **M2.11** | **Change (-+)** | ScheduleFeedback **[E]** | sentiment_score | COUNT WHERE $S_{t-1} = -1$ AND $S_{t} = 1$. |
| **M2.12** | **Change (N+)** | ScheduleFeedback **[E]** | sentiment_score | COUNT WHERE $S_{t-1} = 0$ AND $S_{t} = 1$. |
| **M2.13** | **Change (N-)** | ScheduleFeedback **[E]** | sentiment_score | COUNT WHERE $S_{t-1} = 0$ AND $S_{t} = -1$. |
| **M2.14** | **Change (+-)** | ScheduleFeedback **[E]** | sentiment_score | COUNT WHERE $S_{t-1} = 1$ AND $S_{t} = -1$. |
| **M2.15** | **Change (+N)** | ScheduleFeedback **[E]** | sentiment_score | COUNT WHERE $S_{t-1} = 1$ AND $S_{t} = 0$. |

---

## **3\. Livello 3: Operational Metrics (AI Performance)**

Questa sezione mappa le metriche direttamente sul **JSON Response** definito nel Microtask 3\.

| ID | Variabile Formula | Oggetto JSON (Output AI) | Campo JSON | Note |
| :---- | :---- | :---- | :---- | :---- |
| **M3.1** | **Optimality** | metadata | optimality\_score | Valore float 0.0 \- 1.0 fornito dall'LLM. |
| **M3.2** | **Shift Scoperti** | uncovered\_shifts | Array.length | Conteggio elementi nell'array dei buchi. |
| **M3.3** | **Violazioni Soft** | assignments | is\_forced | COUNT WHERE is\_forced \= true. |
| **M3.4** | **Delta Varianza** | metadata.metrics | uffa\_balance.final \- initial | Differenza calcolata dall'AI tra pre e post scenario. |
| **M3.5** | **Latenza** | metadata | computation\_time\_ms | Tempo di esecuzione della chiamata LLM. 


## Microtask 3.4



## Microtask 3.5



## Microtask 3.6



## Microtask 3.7

- Il parser `AiScheduleJsonParser` espone il fail-on-unknown-properties via costruttore (strict configurabile).
- Se abilitato, proprietà sconosciute causano `SCHEMA_MISMATCH` con categoria `APPLICATION_SCHEMA`.
- Il parser supporta `failOnTypeMismatch` e include il path dell’errore nel messaggio (es. `$.assignments[0].doctor_id`).
- Mismatch di tipo classificati come `APPLICATION_SCHEMA` / `TYPE_MISMATCH`.
