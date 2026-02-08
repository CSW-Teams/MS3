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

## Microtask 4.2: UI Modal di Confronto AI (2×2 Grid)

**Descrizione Task:** Creazione del componente modale che visualizza il confronto tra schedulazioni AI con layout a griglia 2×2.
**Obiettivo:** Fornire una vista compatta delle metriche normalizzate senza etichette, mantenendo coerenza con il tema esistente.

---

### 1. Sintesi del microtask

È stato introdotto un nuovo componente modale dedicato al confronto delle schedulazioni AI. La modale rende una griglia fissa 2×2 con quattro card che mostrano esclusivamente valori metrici normalizzati, senza titoli o label, e con placeholder in caso di dati mancanti.

---

### 2. Componenti Chiave Implementati

#### A. `AiScheduleComparisonModal.js` (Nuovo Componente)

Componente UI per la presentazione dei risultati di confronto.

* **Layout:** Modal centrata con griglia 2×2 (`Grid` + `Card` MUI), responsive e coerente con il tema.
* **Contenuto:** Ogni card mostra solo valori metrici in formato numerico o testuale, senza intestazioni.
* **Placeholder:** In assenza di metriche, viene mostrato un placeholder neutro (es. “—”).
* **Normalizzazione:** Supporta input come array o oggetto di metriche, convertiti in lista ordinata per la visualizzazione.

---

## Microtask 4.3: Integrazione Modale di Confronto nel Flusso UI

**Descrizione Task:** Collegamento della modale di confronto AI al flusso di generazione/rigenerazione.
**Obiettivo:** Aprire la modale automaticamente dopo una generazione AI completata con successo.

---

### 1. Sintesi del microtask

La `ScheduleGeneratorView` è stata estesa per gestire lo stato della modale di confronto e per aprirla quando la generazione o rigenerazione termina con successo. La chiusura è gestita da handler dedicato, mantenendo separata la logica del feedback di stato (success/partial/error).

---

### 2. Componenti Chiave Implementati

#### A. `ScheduleGeneratorView.js` (Integrazione Stato UI)

* **Nuovi stati UI:** `isComparisonOpen` e `comparisonMetrics` per controllare visibilità e contenuti.
* **Trigger di apertura:** La modale viene aperta su `responseStatus === 202` (success) per generazione e rigenerazione.
* **Chiusura dedicata:** Handler separato per la chiusura della modale (`handleCloseComparisonModal`).

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
                
---

## Microtask 4.5: Implement UI error + fallback handling

**1. Descrizione e Obiettivo**

L'obiettivo principale è implementare una gestione degli errori e dei meccanismi di fallback nell'interfaccia utente del Planner, in particolare per scenari legati a metriche mancanti o a fallimenti dell'agente AI. Lo scopo è fornire un feedback chiaro e prevenire un'esperienza utente frammentata in caso di problemi, basandosi sul protocollo di comunicazione definito nella Story 2.

**2. Revisione del Protocollo di Comunicazione AI e Tassonomia degli Errori**

Si sono analizzati i file `ScheduleAPI.js`, `AssegnazioneTurnoAPI.js` e la logica di gestione delle risposte già presente in `ScheduleGeneratorView.js` per comprendere i codici di stato HTTP e le strutture delle risposte (in particolare del `body`) del backend relativi alla generazione e selezione degli schedule AI.

* **Generazione Schedule AI (`postGenerationScheduleAi`):** Gestisce `status 200/202` (successo con candidati nel `body`), `status 206` (successo parziale/warning), `status 406` (errore specifico come schedule già esistente), e altri errori generici (catturati nel `catch` o nel `default` dello `switch`). I messaggi vengono veicolati tramite `generationStatus`, `generationMessage`, `generationDetails` verso `GenerationStatusFeedback.js`.
* **Selezione Schedule (`selectScheduleCandidate`):** `status 202` indica successo; altri stati o errori di rete portano a `toast.error`.
* **Metriche Mancanti:** Non un codice di errore API esplicito, ma piuttosto l'assenza o l'incompletezza di dati all'interno degli oggetti `candidate` passati alla UI.

**3. Identificazione dei Punti di Errore Potenziali nel Flusso UI**

Si sono esaminati a fondo `ScheduleGeneratorView.js` (il componente principale del planner), `AiScheduleComparisonModal.js` (la modale di confronto) e `GenerationStatusFeedback.js` (il componente di feedback sulla generazione).
* `AiScheduleComparisonModal.js` già gestiva robustamente metriche mancanti individuali (mostrando "—"). Rendeva schede vuote se meno di 4 candidati erano disponibili.
* `GenerationStatusFeedback.js` era già molto efficace nel mostrare feedback per gli errori/warning di generazione.
* Le aree chiave su cui intervenire erano il miglioramento dei messaggi di errore più specifici (specialmente i `generationDetails`) e l'aggiunta di un messaggio esplicito nella modale di confronto se non fossero disponibili candidati.

**4. Progettazione della Strategia di Fallback UI e Messaggi di Errore**

* **Per Fallimenti di Generazione AI:** Migliorare l'accuratezza dei `generationDetails` in `ScheduleGeneratorView.js`, estraendo messaggi di errore più specifici dal `response.body` (se disponibili) per i casi di errore 406 e generici.
* **Per Metriche Mancanti (individuali):** L'approccio esistente in `AiScheduleComparisonModal.js` (mostrare "—") è stato ritenuto sufficiente.
* **Per Mancanza Totale di Candidati AI (`candidates` vuoto):** Implementare un messaggio esplicito in `AiScheduleComparisonModal.js` per informare l'utente che non ci sono schedule AI da confrontare, anziché mostrare schede vuote.
* **Per Fallimenti di Selezione:** I `toast.error` esistenti sono stati ritenuti adeguati.

**5. Implementazione della Gestione degli Errori e dei Fallback in `ScheduleGeneratorView.js**`**
Si è modificata la funzione `handleGenerateSchedule` in `frontend/src/views/pianificatore/ScheduleGeneratorView.js`.
* Nel `case 406` dello `switch`, `generationDetails` ora tenta di recuperare un messaggio più specifico da `response.body?.message` prima di ricorrere al messaggio generico `t("Please check dates and existing schedules.")`.
* Nel `default` case dello `switch`, `generationDetails` ora cerca `response.body?.message` o `response.body?.error` prima del messaggio generico `t("An unexpected error occurred.")`.
* Nel blocco `catch (err)`, `generationDetails` ora include `response.body?.message` o `response.body?.error` come fallback aggiuntivo nel caso in cui `err.message` non sia disponibile o sufficientemente descrittivo.

**6. Implementazione dei Fallback in `AiScheduleComparisonModal.js**`**
Si è modificato il componente `AiScheduleComparisonModal.js` in `frontend/src/components/common/AiScheduleComparisonModal.js`.
* È stata aggiunta una logica di rendering condizionale: se l'array `candidates` è vuoto, viene visualizzato un messaggio di `Typography` centrato (`t('No AI-generated schedules available for comparison.')`) invece di mappare e visualizzare le schede vuote con i placeholder.

**7. Aggiornamento di `GenerationStatusFeedback.js` (se necessario)**
* Non sono state ritenute necessarie ulteriori modifiche a questo componente. `GenerationStatusFeedback.js` è già stato progettato per consumare e visualizzare efficacemente le props `status`, `message` e `details`, che ora vengono popolate in modo più specifico dalle modifiche apportate in `ScheduleGeneratorView.js`.

---
