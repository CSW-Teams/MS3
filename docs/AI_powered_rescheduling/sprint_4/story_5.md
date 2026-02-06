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

## Microtask 5.4

## Microtask 5.5

## Microtask 5.6
