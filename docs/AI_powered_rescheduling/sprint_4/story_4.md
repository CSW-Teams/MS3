# Story 4

## Microtask 4.1: Implementazione Stati UI Planner

**Descrizione Task:** Implementazione degli stati e dei flussi dell'interfaccia utente del Planner per la gestione della generazione degli scheduli.
**Obiettivo:** Creare una UI che gestisca visivamente gli stati di *Loading*, *Success*, *Failure* e *Partial Success* (warning).

---

### 1. Sintesi del microtask

Rifattorizzazione di `ScheduleGeneratorView.js`, `BottomViewAggiungiSchedulazione.js` e creazione di componenti UI dedicati per gestire il ciclo di vita della generazione turni.
Le modifiche apportate garantiscono che l'utente riceva feedback immediato e dettagliato durante le interazioni con il backend (AI), superando la precedente gestione basata solo su notifiche "toast" effimere.

---

### 2. Componenti Chiave Implementati

#### A. `ScheduleGeneratorView.js` (Orchestratore, Refactoring)

Questo componente è stato rifattorizzato per gestire gli stati interni.

* **Gestione Stati:** Introdotte variabili di stato per controllare il flusso:
* `isGenerationLoading` (bool): Attiva la modale di blocco.
* `generationStatus` (enum: 'success', 'partial', 'error'): Determina il tipo di feedback.
* `generationDetails` (string): Gestisce il payload dei messaggi (es. lista turni scoperti).


* **Logica API:** I metodi `handleGenerateSchedule` e `handleRegeneration` ora gestiscono la logica di business e mappano gli status code HTTP (202, 206, 406) sui nuovi stati visivi.

#### B. `GenerationLoadingModal.js` (Nuovo Componente)

Componente responsabile del feedback durante l'attesa (che può essere lunga nel caso di generazione AI).

* **UI:** Finestra modale bloccante con `CircularProgress`.
* **UX:** Impedisce interazioni accidentali durante il calcolo e fornisce messaggi di rassicurazione ("The AI is working...").

#### C. `GenerationStatusFeedback.js` (Nuovo Componente)

Componente critico per la visualizzazione dei risultati complessi.

* **Supporto "Partial" (206):** Mappa lo stato `partial` su un alert **giallo/arancione**, permettendo di mostrare i dettagli dei vincoli violati o dei turni scoperti senza bloccare il flusso come un errore fatale.
* **Flessibilità:** Gestisce anche stati di Successo (Verde) ed Errore (Rosso), sostituendo la necessità di molteplici toast popup.

#### D. `BottomViewAggiungiSchedulazione.js` (Refactoring)

Il componente drawer è stato semplificato ("Dumb Component").

* **Delega:** Non effettua più chiamate API dirette. Raccoglie solo l'input (Date) e invoca la callback `onGenerateSchedule` fornita dal padre (`ScheduleGeneratorView.js`).
* **Validazione:** Implementata validazione locale per garantire l'integrità dei dati prima dell'invio.

---

## Microtask 4.3: Modale di conferma selezione schedulazione

**Descrizione Task:** Implementazione della modale di conferma per la selezione finale di uno schedule tra 4 candidati, con copy bilingue EN/IT e blocco selezione dopo conferma.
**Obiettivo:** Rendere esplicita e irreversibile la scelta del Planner, invocando l’endpoint di selezione e disabilitando ulteriori selezioni dopo la conferma.

---

### 1. Sintesi del microtask

Aggiornamento della UI di confronto per mostrare i dettagli dei candidati, aggiunta di una modale di conferma con copy bilingue e wiring completo della selezione con chiamata al backend.  
Dopo la conferma, la selezione viene bloccata e viene conservato un hook per la futura notifica di successo (Story 4.4).

---

### 2. Componenti Chiave Implementati

#### A. `AiScheduleComparisonModal.js` (Aggiornamento layout + CTA)

Il componente di confronto è stato esteso per mostrare i metadati del candidato e abilitare l’azione di selezione.

* **Layout:** 4 card con:
  * etichetta del candidato (Standard, Empatica, Efficiente, Bilanciata)
  * Schedule ID
  * metriche decisionali con label bilingue (coverage, UFFA balance, sentiment transitions, UP delta, variance delta)
* **CTA per selezione:** pulsante “Select schedule / Seleziona schedulazione”.
* **Lock della selezione:** quando lo schedule è confermato, tutte le altre selezioni vengono disabilitate.

#### B. `AiScheduleSelectionConfirmationModal.js` (Nuovo Componente)

Modale di conferma per la scelta finale dello schedule.

* **Copy bilingue EN/IT:** testo scritto da zero per chiarezza e decisione finale.
* **Contesto visivo:** mostra candidato e Schedule ID selezionati.
* **Azioni:** “Cancel / Annulla” chiude la modale senza effetti, “Confirm selection / Conferma selezione” finalizza la scelta.

#### C. `ScheduleGeneratorView.js` (Orchestrazione selezione)

Il componente orchestratore gestisce lo stato di selezione e l’invocazione del backend.

* **Nuovi stati:** `comparisonCandidates`, `selectionLocked`, `selectedCandidateKey`, `pendingCandidate`, `isSelectionConfirmationOpen`, `isSelectionSubmitting`, `selectedScheduleId`.
* **Flusso selezione:** click su una card → apertura modale → conferma → chiamata API → lock selezione.
* **Hook per Story 4.4:** `selectedScheduleId` mantenuto per messaggi di successo futuri.

#### D. `ScheduleAPI.js` (Endpoint di selezione)

Nuovo metodo API per invocare l’endpoint di selezione:

* **POST** `/api/schedule/selection`
* **Payload:** `{ candidateId }` (o label del candidato)
* **Output:** status HTTP e body di risposta per il lock UI

---
