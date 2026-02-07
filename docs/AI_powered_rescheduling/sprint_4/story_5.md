# Story 5

## Backend Orchestration Plan for AI Rescheduling

**Goal:** Definire ed implementare l’orchestrazione backend che genera 1 schedule standard + 3 schedule AI, valida i payload TOON/JSON, calcola metriche e espone gli endpoint di confronto e selezione.

**Sintesi del contesto (allineata alle Story 1–4)**
- **Story 1 (Baseline):** ha definito il flusso standard di generazione schedule e i componenti/DAO coinvolti; Story 5 estende tale pipeline con un livello di orchestrazione AI.
- **Story 2 (Protocollo AI / TOON):** ha stabilito formato TOON in input e JSON in output, con validazioni e vincoli GDPR; Story 5 lo integra in un flusso end‑to‑end con gate di pre/post‑validazione prima/dopo le chiamate AI.
- **Story 3 (Metriche):** ha definito le metriche di confronto e i requisiti di valutazione; Story 5 calcola e persiste tali metriche per esporle via API.
- **Story 4 (UI):** ha introdotto gli stati UI per success/partial/failure; Story 5 deve fornire status e error handling coerenti con questi stati.

**Functional Scope**
- Sequenziamento della generazione (standard + AI) e raccolta risultati.
- Generazione TOON e parsing/validazione JSON.
- Calcolo metriche di confronto e persistenza.
- Endpoint di confronto, selezione e commit dello schedule scelto.
- Gestione errori, retry, e audit logging.

**Deliverable attesi**
- Orchestrazione backend completa (servizio + pipeline).
- Validazioni TOON/JSON integrate nel flusso.
- Metriche esposte via API con schema coerente a Story 3.
- Endpoint di selezione e logging/audit per tracciabilità.

**Sintesi operativa della pipeline (alto livello)**
1. Generazione schedule standard (baseline Story 1).
2. Raccolta dati + generazione TOON (Story 2.3 / 2.6).
3. Gate di validazione pre‑AI (Story 2.6 / 2.5).
4. Chiamate ai provider AI via broker (Story 2.8).
5. Parsing/validazione JSON risposta (Story 2.7).
6. Calcolo metriche e ranking (Story 3).
7. Esposizione API di confronto e selezione, con status compatibili UI (Story 4).

---

## Microtask 5.1

## Microtask 5.2

**Implement TOON request generation + validation (2h)**

**Descrizione**
Implementare la generazione della richiesta `.toon` a partire dallo stato corrente dello scheduling e inserire **gate di validazione** prima di qualunque chiamata AI. La pipeline deve essere integrata **nel service di orchestrazione** (Story 5), il più a monte possibile, così da applicare pseudonimizzazione e normalizzazione dati prima di costruire il `ToonRequestContext`.

**Precondizioni**
- Story 2 completata (TOON schema, validator, JSON protocol e broker).
- Story 1 baseline disponibile per data sources e pipeline standard.

**Affinità in parallelo**
- Nessuna.

**Output artifact**
- Pipeline di generazione + validazione TOON con gate di blocco prima della chiamata AI.

---

### 1) Scope tecnico del microtask

**1.1 Orchestrazione (early stage)**
L’orchestrazione deve raccogliere **tutti i dataset necessari** e applicare trasformazioni prima di invocare il builder TOON:

```
● schedule window (period, mode)
● shifts (ConcreteShift target)
● doctors (pool + seniority + priorities)
● blocks (preferences/unavailability)
● holidays / doctor holidays
● active constraints (precomputati)
● schedule feedbacks (doctor feedback)
```

**1.2 Feedback di schedulazione (doctor schedule feedback)**
La fonte dei feedback **deve essere la stessa** usata per l’inserimento dei feedback dai dottori.

Ogni feedback è composto da:

```
● category (strutturata / enumerata)
● rating (strutturato, 6-star)
● comment (testuale libero)
```

La pipeline deve:

```
● mantenere il comment come testo (non rimosso)
● imporre che category e rating siano strutturati e validi (rating 1–6)
```

**1.3 Pseudonimizzazione**
La pseudonimizzazione degli identificativi medico **deve avvenire il più presto possibile** nell’orchestrazione, prima di costruire il `ToonRequestContext`, ed essere applicata in modo coerente a:

```
● doctors
● feedbacks
● any reference to doctor_id in constraints
```

La mappatura deve essere request‑scoped e reversibile solo all’interno della pipeline di orchestrazione.

---

### 2) Pipeline richiesta TOON (pre‑AI)

**Step A — Build `ToonRequestContext`**
Creare il contesto secondo lo schema definito nella Story 2.3 (metadati sessione, shifts, doctors, blocks, active_constraints, feedbacks).

**Step B — Preflight Validation (pre‑serialization)**
Validare tutti i campi del contesto prima della serializzazione:

```
● required fields presenti
● referential integrity (shift_id, doctor_id)
● rating feedback nel range 1–6
● category feedback in set ammesso
● no duplicazioni critiche (shift_id / doctor_id)
```

**Step C — Serialization TOON**
Serializzare con `ToonBuilder` (Story 2.6).

**Step D — Post‑serialization Validation**
Validare il TOON generato:

```
● sezioni obbligatorie presenti
● ordine/struttura conformi allo schema 2.3
● controllo GDPR/PII (guardrail)
```

**Step E — Gate**
Se una delle validazioni fallisce, **bloccare la chiamata AI** e ritornare un errore strutturato.

---

### 3) Compliance GDPR / Data Minimization (Story 2.5)

**Allineamenti richiesti**

```
● doctor_id pseudonimizzato (request‑scoped)
● no dati identificativi diretti nel TOON
● feedback: category/rating strutturati, comment testuale ammesso
```

Nota: l’uso del comment testuale è permesso, ma deve rimanere confinato a contenuti rilevanti per la schedulazione.

---

### 4) Test minimi attesi

```
● TOON generation OK con feedback (category/rating/comment)
● failure su category non valida
● failure su rating fuori range
● pseudonimizzazione coerente tra doctors e feedbacks
● gate: nessuna chiamata AI se validation fails
```

## Microtask 5.3

**Implement JSON response ingestion + validation (2h)**

**Descrizione**
Implementare il parsing, la validazione e la conversione delle risposte JSON generate dall'AI in modelli interni del dominio. Questo include la gestione degli `AiAssignmentDto` per creare entità `ConcreteShift` e `DoctorAssignment`, inferendo le `Task` appropriate.

**Precondizioni**
- Story 2 completata (protocollo AI, formati TOON/JSON, validazioni).

**Affinità in parallelo**
- Nessuna.

**Output artifact**
- Pipeline di ingestione JSON completamente implementata e integrabile nel servizio di orchestrazione.

---

### Cosa è stato implementato

*   **Servizio `AiScheduleConverterService`:** È stata creata una nuova classe `AiScheduleConverterService.java` nel package `org.cswteams.ms3.ai.protocol.converter`. Questo servizio orchestra l'intero processo di ingestione del JSON dell'AI.
*   **Parsing e Validazione:** Il servizio utilizza `AiScheduleJsonParser` (per il parsing sintattico) e `AiScheduleSemanticValidator` (per la validazione semantica) per assicurare che la risposta AI sia ben formata e coerente. Sono state aggiunte le annotazioni `@Service` a entrambi questi componenti per permettere l'autowiring.
*   **Conversione a Modelli Interni:** Il cuore del servizio è la mappatura dei `AiAssignmentDto` (presenti nella risposta AI) in entità del dominio `ConcreteShift` e `DoctorAssignment`.
    *   **Estrazione `Shift` e Data:** Il servizio analizza il campo `shiftId` dal formato `S_<id>_<yyyyMMdd>` per estrarre l'ID del template `Shift` e la data specifica dell'assegnazione.
    *   **Risoluzione Entità:** Vengono risolte le entità `Doctor` e `Shift` (template) usando i rispettivi DAOs (`DoctorDAO`, `ShiftDAO`). È stata gestita la non conformità del metodo `findById` di `DoctorDAO` che restituisce direttamente l'entità o `null` anziché un `Optional`.
    *   **Inferenza `Task`:** È stata implementata una logica per inferire l'entità `Task` associata all'assegnazione, basandosi sul template `Shift` e sulla `Seniority` del medico (`roleCovered`). In caso di ambiguità o impossibilità di risoluzione, viene lanciata un'eccezione specifica.
    *   **Creazione `ConcreteShift` e `DoctorAssignment`:** Vengono create le entità `ConcreteShift` (raggruppando le assegnazioni per lo stesso turno concreto) e `DoctorAssignment`, collegandole correttamente con `Doctor`, `ConcreteShift` e `Task`.
*   **Gestione Errori:** Tutte le fasi di parsing, validazione e conversione sono dotate di robusta gestione degli errori, lanciando `AiProtocolException` con codici specifici (`ENTITY_NOT_FOUND`, `INVALID_FORMAT`, `TASK_RESOLUTION_ERROR`) per indicare problemi durante il processo. Per supportare ciò, `AiProtocolException.java` è stata modificata per includere i metodi factory statici e i `ErrorCode` corrispondenti.
*   **Adattamento Java 11:** La classe `ParsedShiftId` è stata implementata come una classe statica interna invece di un `record` (funzionalità Java 16), per compatibilità con l'ambiente di compilazione Java 11 del progetto.

### Testing non eseguito (non richiesto)
Non sono stati eseguiti test automatici specifici per questo microtask.

## Microtask 5.4

**Implement Comparison + Selection Endpoints (2h)**

**Obiettivo**
Esporre gli endpoint REST necessari per ottenere il confronto tra le schedulazioni (standard + AI) e per
confermare la schedulazione selezionata dal planner, garantendo la compatibilità con la UI dello Story 4 e
con il flusso di orchestrazione definito in Story 5.

**Scelte architetturali**
- **Controller thin e logica nel service:** i controller REST delegano la logica di selezione e di recupero
  del confronto al service di orchestrazione, mantenendo il layer REST minimale e coerente con il pattern
  esistente in `ScheduleRestEndpoint`.
- **Stato di confronto transiente:** viene mantenuto in memoria lo stato dell’ultimo confronto generato per
  permettere il recupero dei candidati e la risoluzione dei `candidateId` forniti dalla UI. Lo stato
  transiente contiene anche la risposta serializzata da restituire al GET `/api/comparison`.
- **Selezione flessibile:** il payload di selezione accetta **`scheduleId` o `candidateId`**. Se arriva
  `candidateId`, la risoluzione avviene sullo stato di confronto. Se la risoluzione fallisce, si risponde
  con `409 CONFLICT` e un errore strutturato.
- **Compatibilità con rigenerazione:** la selezione finale persiste lo schedule scelto tramite lo stesso
  controller di scheduling, in modo da rispettare le regole di generazione/rigenerazione già esistenti e
  non duplicare logica di persistenza.

**Classi create / modificate**
- **`ComparisonRestEndpoint`**  
  Nuovo controller REST che espone:
  - `GET /api/comparison`: restituisce l’ultimo confronto disponibile, includendo candidati, metriche raw e
    normalizzate, e decision outcome.
  - `POST /api/comparison/selection`: conferma la scelta del planner; accetta `scheduleId` o `candidateId`
    ed esegue la persistenza della schedulazione selezionata.  
  Serve come endpoint dedicato al confronto, separando l’API AI dal flusso storico di scheduling.

- **`AiScheduleSelectionRequestDto`**  
  Esteso per includere `scheduleId` oltre a `candidateId`. Questo adeguamento consente alla UI di inviare
  direttamente l’identificativo persistito quando disponibile, riducendo l’ambiguità nel binding del
  candidato.

- **`AiScheduleGenerationOrchestrationService`**  
  Esteso con:
  - **Cache della risposta di confronto:** salvataggio dell’ultimo confronto in stato transiente per
    il recupero via `GET /api/comparison`.
  - **Metodo `getLatestComparison()`:** restituisce la risposta pronta da inviare al client.
  - **Metodo `selectSchedule(...)`:** punto centrale di selezione che accetta il DTO, valida il payload,
    risolve `candidateId` in schedule effettivo e persiste la selezione.
  - **Gestione errori esplicita:** ritorno di errori con `errorCode` e messaggi chiari per casi come
    `NO_ACTIVE_COMPARISON` o `CANDIDATE_NOT_FOUND`, così da supportare la UI negli stati di errore.

- **`ScheduleRestEndpoint`**  
  Aggiornato per delegare la selezione al nuovo metodo di orchestrazione; mantiene compatibilità con il
  vecchio endpoint `/schedule/selection`, ma riutilizza la stessa logica comune di selezione.

**Perché questi cambiamenti rispetto a quanto già esisteva**
- Il flusso precedente gestiva solo la selezione tramite `candidateId`, senza endpoint di confronto dedicato
  e senza un meccanismo di recupero del confronto a valle della generazione AI.
- La UI della Story 4 richiede endpoint espliciti per ottenere il confronto e per selezionare lo schedule
  scelto, con risposta coerente anche in caso di errori di risoluzione.
- Centralizzare la selezione in orchestrazione evita duplicazioni fra endpoint e garantisce che la
  persistenza dello schedule avvenga con le stesse regole di validazione già consolidate nello scheduler
  controller.

**Testing / integrazione**
Sono stati aggiunti test di integrazione per coprire:
- `GET /api/comparison` con payload valido.
- `POST /api/comparison/selection` con `scheduleId`.
- `POST /api/comparison/selection` con `candidateId`.
- `POST /api/comparison/selection` con `candidateId` non risolvibile e risposta `409 CONFLICT`.


## Microtask 5.5

## Microtask 5.6
