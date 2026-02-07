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

## Microtask 4.4: Wire success message reuse
### 1. Descrizione e Obiettivo del Task
L'obiettivo principale è integrare un pattern di notifica di successo già esistente all'interno del flusso di selezione dello schedule nel Planner UI, dopo che un utente ha completato la scelta di uno schedule. Questo per garantire coerenza nell'esperienza utente e riutilizzare codice esistente. La precondizione è l'inventario dell'interfaccia utente della Story 1.

### 2. Comprendere il Pattern di Notifica di Successo Esistente

Si fa un uso estensivo della libreria `react-toastify`, in numerosi file, le notifiche di successo vengono mostrate tramite la funzione `toast.success(message, options)`.

### 3. Identificare il Punto di Innesco per il Messaggio di Successo

Esaminando il contesto della "Story 4" e in particolare del "Microtask 3: Implement selection + confirmation modal", capiamo che il messaggio di successo deve apparire dopo che l'utente ha confermato la selezione di uno schedule. Il componente UI principale del planner è `frontend/src/views/pianificatore/ScheduleGeneratorView.js`.
All'interno di `ScheduleGeneratorView.js` c'è la funzione `handleConfirmSelection`. Questa funzione è responsabile della gestione della conferma della selezione di uno schedule e, al suo interno, effettua una chiamata all'API `ScheduleAPI().selectScheduleCandidate()`. Il messaggio di successo deve essere visualizzato immediatamente dopo che questa chiamata API restituisce uno stato 202 (HTTP Accepted), indicando una selezione riuscita. 

### 4. Integrare la Notifica di Successo

Confermato il pattern (`toast.success`) e il punto di innesco (`if (response.status === 202)` in `handleConfirmSelection`), si aggiunge il seguente blocco di codice all'interno del blocco `if (response.status === 202)` della funzione `handleConfirmSelection` in`frontend/src/views/pianificatore/ScheduleGeneratorView.js`:

              toast.success(t("Schedule successfully selected!"), {
                position: "top-center",
                autoClose: 5000,
                hideProgressBar: true,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
                }
