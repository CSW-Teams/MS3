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
