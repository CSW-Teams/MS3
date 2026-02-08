# **Story 1 
## Microtask 1.1 — Backend**

**Scheduling Flow (Baseline)**

**1) Componenti principali e responsabilità**

**_1.1 REST Layer — ScheduleRestEndpoint_**

Il layer REST espone le API per:

```
● generazione ( POST /schedule/generation )
```
```
● rigenerazione ( POST /schedule/regeneration/id={id} )
```
```
● lettura scheduli ( GET /schedule/ , GET /schedule//dates/ , GET /schedule/illegals )
```
```
● eliminazione ( DELETE /schedule/id={id} )
```
Caratteristiche:

```
● l’endpoint è thin (logica minima di validazione date / id)
```
```
● delega la business logic a ISchedulerController
```
```
● non restituisce lo schedule in response per generation/regeneration: solo HTTP status
code
```
```
○ implica che la UI debba poi usare GET /schedule/ o /dates/ per ottenere la lista
aggiornata
```
**_1.2 Application Orchestrator — ISchedulerController_**

Interfaccia che definisce le operazioni di scheduling e gestione schedule:

```
● createSchedule(start, end) (proxy)
```
```
● createSchedule(start, end, doctorUffaPriorityList, snapshot) (full)
```
```
● recreateSchedule(id)
```
```
● metodi di gestione “manuale” dei concreteshift (add/modify/remove)
```
**_1.3 Implementazione — SchedulerController_**


SchedulerController è un **@Service** e rappresenta l’orchestratore reale della pipeline di
generazione:

```
● raccoglie dati dai DAO
```
```
● costruisce le ConcreteShift nel range date×shift
```
```
● inizializza ScheduleBuilder
```
```
● configura il controller delle priorità (“scocciatura”)
```
```
● invoca scheduleBuilder.build()
```
```
● persiste schedule e priorità aggiornate
```
**2) Data sources toccate (DAO) durante la generazione**

Durante createSchedule(...) vengono letti i seguenti dataset:

```
● ShiftDAO : lista turni astratti (template) da espandere nel periodo
```
```
● ConstraintDAO : vincoli attivi
```
```
● DoctorDAO : lista medici schedulabili
```
```
● HolidayDAO : festività
```
```
● DoctorHolidaysDAO : associazioni medico–festività
```
```
● ScocciaturaDAO : configurazione/definizione scocciature per controller priorità
```
```
● DoctorUffaPriorityDAO : priorità correnti dei medici (code)
```
```
● DoctorUffaPrioritySnapshotDAO : snapshot priorità (per reset in rigenerazione)
```
```
● ScheduleDAO : per controlli di duplicati e persistenza
```
```
Nota: sono presenti anche DoctorAssignmentDAO, ConcreteShiftDAO, TaskDAO,
ma sono coinvolti soprattutto in operazioni di modifica manuale dei concreteshift più
che nella generazione “full”.
```
**3) Generazione schedule — sequenza reale**


**_3.1 Trigger REST → orchestratore_**

1. Client invia **POST /schedule/generation** con **ScheduleGenerationDTO {startDate,**
    **endDate}**
2. REST valida:

```
○ body non nullo
```
```
○ endDate >= startDate
```
3. REST chiama **schedulerController.createSchedule(startDate, endDate)**

**_3.2 createSchedule(start, end) (proxy)_**

**SchedulerController.createSchedule(startDate, endDate):**

```
● carica:
```
```
○ DoctorUffaPriorityDAO.findAll()
```
```
○ DoctorUffaPrioritySnapshotDAO.findAll()
```
```
● invoca la variante completa:
```
```
○ createSchedule(start, end, doctorUffaPriorityList, snapshot)
```
**_3.3 createSchedule(start, end, priorities, snapshot) (full pipeline)_**

**_Step A — policy di ammissibilità iniziale_**

```
● se non esistono schedule già creati e startDate < today, ritorna null
```
```
○ “non consentire schedulazioni iniziali nel passato”
```
**_Step B — deduplica intervallo_**

```
● alreadyExistsAnotherSchedule(start, end) blocca solo duplicati esatti
```
```
○ se esiste schedule con stesso start e stesso end → null
```
```
○ attenzione: overlap e adiacenze sono consentiti
```
**_Step C — costruzione ConcreteShift (espansione date×shift)_**


```
● per ogni giorno nel range:
```
```
○ per ogni Shift in shiftDAO.findAll():
```
```
■ se il giorno della settimana è ammesso da shift.getDaysOfWeek()
```
```
■ crea new ConcreteShift(currentDay.toEpochDay(), shift)
```
```
● risultato: lista allConcreteShifts senza medici assegnati
```
**_Step D — init ScheduleBuilder (core engine)_**

Viene costruito ScheduleBuilder con:

```
● start/end
```
```
● vincoli constraintDAO.findAll()
```
```
● concreteshift generati
```
```
● medici doctorDAO.findAll()
```
```
● festività holidayDAO.findAll()
```
```
● associazioni ferie/festività medici doctorHolidaysDAO.findAll()
```
```
● priorità doctorUffaPriorityList
```
```
● snapshot snapshot (che verrà aggiornato/salvato)
```
**_Step E — setup priorità (“scocciatura”)_**

```
● carica scocciaturaDAO.findAll()
```
```
● costruisce ControllerScocciatura(scocciaturaList)
```
```
● imposta scheduleBuilder.setControllerScocciatura(controllerScocciatura)
```
**_Step F — build schedule_**

```
● Schedule schedule = scheduleBuilder.build()
```
**_Step G — persistenza_**

```
● scheduleDAO.save(schedule)
```

```
● per ogni DoctorUffaPriority dup : schedule.getDoctorUffaPriorityList() :
```
```
○ dup.setSchedule(schedule)
```
```
○ doctorUffaPriorityDAO.save(dup)
```
**_Error handling_**

```
● se ScheduleBuilder.build() lancia IllegalScheduleException:
```
```
○ ritorna null
```
```
● il REST mapperà null a 406 NOT_ACCEPTABLE
```
**4) Rigenerazione schedule — sequenza reale**

**_4.1 Trigger REST → orchestratore_**

1. Client invia **POST /schedule/regeneration/id={id}**
2. REST chiama **schedulerController.recreateSchedule(id)**
3. REST ritorna:

```
○ 202 ACCEPTED se true
```
```
○ 417 EXPECTATION_FAILED se false
```
```
○ 400 BAD_REQUEST su eccezione/parametri errati (nel REST è previsto
UnableToBuildScheduleException, ma l’implementazione mostrata non la lancia)
```
**_4.2 recreateSchedule(id)_**

Pipeline:

1. **scheduleDAO.findById(id)** → se vuoto: false
2. ricava start/end da epochDay salvato nello schedule
3. carica snapshot e priorità correnti:

```
○ DoctorUffaPrioritySnapshotDAO.findAll()
```

```
○ DoctorUffaPriorityDAO.findAll()
```
4. ripristina priorità:

```
○ per ogni snapshot, trova la corrispondente priorità per medico e copia:
```
```
■ generalPriority
```
```
■ nightPriority
```
```
■ longShiftPriority
```
5. elimina lo schedule:

```
○ removeSchedule(id) (blocca eliminazione se lo schedule è nel passato)
```
```
○ se fallisce: false
```
6. rigenera:

```
○ createSchedule(startDate,endDate,doctorUffaPriorityList,
```
```
doctorUffaPrioritySnapshot)
```
7. ritorna true

**5) Implicazioni “baseline” utili per AI-rescheduling**

**(Sprint 4/5)**

**_5.1 Hook naturale per l’AI: SchedulerController / ScheduleBuilder_**

La generazione è già centralizzata in un orchestratore unico:

```
● integrare “1 standard + 3 AI schedules” sarà più semplice intervenendo nel layer di
orchestrazione (Story 5).
```
**_5.2 Status-only su generation/regeneration_**

Poiché gli endpoint POST non ritornano body:

```
● qualunque UI di “schedule comparison” dovrà appoggiarsi a:
```
```
○ nuovi endpoint (es. /comparison)
```

```
○ oppure estensioni di GET /schedule/ e/o GET /schedule//dates/
```
**_5.3 Gestione vincoli violati_**

Il REST distingue:

```
● schedule nullo → 406
```
```
● schedule con violatedConstraints non vuote → 206 PARTIAL_CONTENT
```
Questo pattern può essere riusato per:

```
● schedule AI “parziali”
```
```
● fallback / explainability
```

- microtask 1.
- microtask 1.


## Microtask 1.2 — Vincoli e pipeline priorità (Baseline)**

Questa analisi descrive il comportamento **effettivo** del sistema di scheduling, distinguendo
tra la semantica dichiarata dei vincoli e delle priorità e la semantica realmente implementata
nel codice a runtime.

**Pipeline dei vincoli (flow a runtime)**

La generazione scheduli prepara un ScheduleBuilder con i dati di dominio (turni concreti,
medici, festività) e la lista dei vincoli letti da persistenza. In ogni tentativo di assegnazione, il

builder costruisce un ContextConstraint che combina: il medico candidato

(DoctorUffaPriority), il ConcreteShift target, la mappa ferie/festività del medico

(DoctorHolidays) e l’elenco delle festività di sistema. Questo oggetto è il contesto unico

passato a tutti i vincoli per decidere se l’assegnazione è ammissibile.

【F:src/main/java/org/cswteams/ms3/control/scheduler/SchedulerController.java†L98-L148】

【F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L64-L159】

【F:src/main/java/org/cswteams/ms3/entity/constraint/ContextConstraint.java†L1-L41】

Durante addDoctors, il metodo verifyAllConstraints(context, false) scorre

_tutti_ i vincoli e, alla prima violazione, invalida il candidato. È importante notare che la

generazione invoca sempre isForced=false, quindi anche i vincoli marcati come
“violabili” si comportano come hard constraint durante il build standard.

【F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L297-L389】【
F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L424-L456】

Quando i candidati ammissibili non sono sufficienti, la pipeline lancia
NotEnoughFeasibleUsersException. Nel caso di medici “on duty” l’eccezione blocca la

costruzione, marca lo schedule come illegale e interrompe il riempimento di quel turno; per
la reperibilità (“on call”) invece l’eccezione viene loggata e la generazione prosegue,
lasciando la copertura incompleta ma non annullando l’intero schedule.

【F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L171-L226】

**Catalogo vincoli attivi (implementazione corrente)**

Il set di vincoli applicati oggi è costituito da:

```
● ConstraintUbiquita : impedisce sovrapposizioni temporali tra turni dello stesso
medico (nessuna intersezione di finestre temporali).
```
```
【F:src/main/java/org/cswteams/ms3/entity/constraint/ConstraintUbiquita.java†L1-L44】
```
```
● ConstraintMaxOrePeriodo : limita i minuti totali lavorati in una finestra di giorni
configurabile (durata finestra + max minuti).
```

```
【F:src/main/java/org/cswteams/ms3/entity/constraint/ConstraintMaxOrePeriodo.java†L1-L82】
```
```
● ConstraintMaxPeriodoConsecutivo : limita il numero di minuti consecutivi lavorabili;
può essere parametrizzato per categoria/condizione del medico (es. over-62,
gravidanza).
```
```
【F:src/main/java/org/cswteams/ms3/entity/constraint/ConstraintMaxPeriodoConsecutivo.java†L1-L146】
```
```
● ConstraintTurniContigui : vieta turni troppo ravvicinati in base a un time slot “trigger”
e a un orizzonte temporale (es. blocco dopo notte).
```
```
【F:src/main/java/org/cswteams/ms3/entity/constraint/ConstraintTurniContigui.java†L1-L109】
```
```
● ConstraintHoliday : evita che un medico venga assegnato alla stessa festività già
coperta l’anno precedente, usando la mappa festività per medico.
```
```
【F:src/main/java/org/cswteams/ms3/entity/constraint/ConstraintHoliday.java†L1-L43】
```
```
● ConstraintNumeroDiRuoloTurno : garantisce i numeri minimi per seniority su
guardia/reperibilità dentro un turno concreto (specialista, strutturato, ecc.).
```
```
【F:src/main/java/org/cswteams/ms3/entity/constraint/ConstraintNumeroDiRuoloTurno.java†L1-L133】
```
```
● AdditionalConstraint è presente ma attualmente privo di logica di verifica
(placeholder).
```
```
【F:src/main/java/org/cswteams/ms3/entity/constraint/AdditionalConstraint.java†L1-L15】
```
**Pipeline priorità (UFFA/scocciatura)**

Il sistema delle priorità UFFA mantiene, per ogni medico, tre code indipendenti (GENERAL,

LONG_SHIFT, NIGHT). Ogni coda ha un valore persistente e un valore “parziale” usato per
ordinare i candidati. Le soglie di normalizzazione e i limiti assoluti (upper/lower bound) sono

letti da priority.properties.

【F:src/main/java/org/cswteams/ms3/entity/DoctorUffaPriority.java†L1-L169】

【F:src/main/java/org/cswteams/ms3/control/scocciatura/ControllerScocciatura.java†L18-L54】

【F:src/main/resources/priority.properties†L1-L35】

La sequenza operativa è la seguente:

1. All’avvio del build, il builder copia lo stato corrente delle priorità in uno snapshot
    (necessario per rigenerazioni future) e normalizza i valori per coda in modo che il
    minimo diventi 0, se il controller scocciatura è disponibile.

```
【F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L155-L189】
```
```
【F:src/main/java/org/cswteams/ms3/control/scocciatura/ControllerScocciatura.java†L98-L138】
```

2. Per ogni ConcreteShift, il controller calcola un delta di “uffa” per ciascun medico
    (calcolaUffaComplessivoUtenteAssegnazione) e aggiorna i valori _parziali_
    della coda interessata. L’ordinamento viene applicato sempre sulla coda GENERAL,
    sulla LONG_SHIFT solo se esiste una mattina contigua (possibile long shift), e sulla
    NIGHT solo per turni notturni.

```
【F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L262-L323】
```
```
【F:src/main/java/org/cswteams/ms3/control/scocciatura/ControllerScocciatura.java†L56-L97】
```
3. Il loop di assegnazione consuma la lista ordinata; quando un medico viene scelto, i
    valori “persistenti” della coda vengono aggiornati con updatePriority, con la
    logica speciale per long shift (mattina+afternoon) e turni notturni.

```
【F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L325-L389】
```
```
【F:src/main/java/org/cswteams/ms3/entity/DoctorUffaPriority.java†L102-L169】
```
Le “scocciature” che generano i delta sono entità persistenti: penalità per giorno/time slot
(es. weekend o fasce specifiche), per desiderata non rispettate, e per festività/fasce orarie. Il
delta complessivo è la somma dei pesi delle scocciature applicabili al contesto corrente.

【F:src/main/java/org/cswteams/ms3/control/scocciatura/ControllerScocciatura.java†L56-L97】

【F:src/main/java/org/cswteams/ms3/entity/scocciature/ScocciaturaAssegnazioneUtente.java†L1-L49】

【F:src/main/java/org/cswteams/ms3/entity/scocciature/ScocciaturaDesiderata.java†L1-L46】

【F:src/main/java/org/cswteams/ms3/entity/scocciature/ScocciaturaVacanza.java†L1-L51】

**Rischi / comportamenti rilevanti (in ottica AI-rescheduling)**

```
● Vincoli soft trattati come hard : la generazione standard usa sempre
verifyAllConstraints(..., false), quindi ogni violazione invalida il
candidato anche se il vincolo è “violabile”. Potrebbe restringere eccessivamente le
schedule AI “parziali” o comparative, a meno di introdurre un percorso “forced”
esplicito.
```
```
【F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L297-L456】
```
```
● Violazioni non registrate : verifyAllConstraints non popola
Schedule.violatedConstraints, ma il REST usa quella lista per ritornare 206
PARTIAL_CONTENT. Oggi il planner non vede la lista violazioni in output, con impatto
su metriche e explainability.
```
```
【F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L424-L456】
```
```
【F:src/main/java/org/cswteams/ms3/rest/ScheduleRestEndpoint.java†L28-L53】
```

```
● Assunzione di DoctorHolidays non nullo : ConstraintHoliday dereferenzia
context.getDoctorHolidays() senza null-check; se mancano record, la
generazione può fallire a runtime.
```
```
【F:src/main/java/org/cswteams/ms3/entity/constraint/ConstraintHoliday.java†L17-L41】
```
```
【F:src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java†L373-L421】
```
```
● Ordinamento non deterministico : prima del sort c’è uno shuffle della lista, quindi a
parità di dati due run possono produrre schedule diverse. Questo riduce la ripetibilità
delle comparazioni AI se non si introduce un seed o un ordering stabile.
```
```
【F:src/main/java/org/cswteams/ms3/control/scocciatura/ControllerScocciatura.java†L68-L92】
```
```
● Dipendenza dal path filesystem : priority.properties viene letto tramite path
locale, che può rompersi in runtime se il working directory o il packaging non
corrispondono al layout di sviluppo.
```
```
【F:src/main/java/org/cswteams/ms3/control/scocciatura/ControllerScocciatura.java†L28-L54】
```
```
【F:src/main/resources/priority.properties†L1-L35】
```
Questi aspetti costituiscono la **baseline tecnica** su cui progettare meccanismi di _AI-assisted
rescheduling_ , valutazione comparativa tra schedule alternative e introduzione di
explainability e metriche di qualità nella fase di ripianificazione.


## Microtask 1.3**

**Analisi delle superfici UI per la schedulazione (Planner)**

**1. Obiettivo e perimetro**

Questo documento analizza in modo dettagliato le **superfici UI coinvolte nel flusso di
schedulazione** lato Planner, con riferimento esplicito alle **classi React** , alle **API frontend** e
agli **endpoint REST** utilizzati.

L’obiettivo è fornire:

```
● una mappa tecnica delle interazioni UI ↔ backend
```
```
● un punto di partenza solido per l’introduzione del rescheduling AI-based
```
**2. Generazione dello schedulo**

**2.1 Componenti coinvolti**

La generazione di uno schedulo è implementata tramite i seguenti componenti frontend:

```
● SchedulerGeneratorView
Responsabile della visualizzazione della lista scheduli e del pulsante di creazione.
```
```
● TemporaryDrawerSchedulo (BottomViewAggiungiSchedulazione)
Componente funzionale che implementa la drawer di inserimento date.
```
```
● AssegnazioneTurnoAPI.postGenerationSchedule()
Wrapper API frontend per la chiamata REST di generazione.
```
**2.2 Flusso UI → Backend**

1. Il Planner apre la drawer tramite il pulsante “Create schedule”
2. Il componente TemporaryDrawerSchedulo raccoglie:


```
○ dataInizio
```
```
○ dataFine
```
3. Alla conferma viene invocato:

AssegnazioneTurnoAPI.postGenerationSchedule(dataInizio, dataFine)

4. Il metodo costruisce un payload con:

```
○ initialDay, initialMonth, initialYear
```
```
○ finalDay, finalMonth, finalYear
```
5. La richiesta viene inviata a:

POST /api/schedule/generation

**2.3 Gestione del risultato**

Il frontend **non riceve lo schedulo generato** , ma interpreta esclusivamente lo status HTTP:

```
Status Significato UI Codice
```
```
202 Schedulo creato
correttamente
```
```
toast.succes
s
```
```
206 Schedulo incompleto toast.warning
```
```
406 Schedulo duplicato toast.error
```
```
altro Errore generico toast.error
```
Questa scelta progettuale implica che:

```
● la UI non conosce i dettagli delle violazioni
```
```
● non è possibile mostrare metriche o spiegazioni strutturate
```

**3. Gestione degli scheduli**

**3.1 Componenti e API**

La gestione degli scheduli è centralizzata in:

```
● SchedulerGeneratorView
```
```
● ScheduleAPI
```
Metodi principali:

```
● getSchedulazini()
```
```
● deleteSchedule(id)
```
```
● rigeneraSchedule(id)
```
Endpoint backend corrispondenti:

```
● GET /api/schedule/
```
```
● DELETE /api/schedule/id={id}
```
```
● POST /api/schedule/regeneration/id={id}
```
**3.2 Regole UI codificate**

Nel codice frontend è esplicitamente codificata la regola:

```
Solo l’ultimo schedulo può essere rigenerato
```
schedule ===

this.state.schedulazioni[this.state.schedulazioni.length - 1]

Questo vincolo UI:

```
● riflette una scelta di dominio
```

```
● anticipa un concetto di “schedulo corrente”
```
```
● sarà rilevante per il confronto multi-schedulo AI
```
**3.3 Eliminazione schedulo**

Alla cancellazione:

1. viene mostrata una loading overlay
2. viene inviata una DELETE
3. la UI reagisce in base allo status (200, 400, 417)
4. la lista viene ricaricata tramite componentDidMount()

Non è presente:

```
● undo
```
```
● soft delete
```
```
● conferma esplicita
```
**4. Visualizzazione e modifica dei turni**

**4.1 Componenti principali**

La visualizzazione operativa utilizza:

```
● ScheduleView
```
```
● AssegnazioneTurnoAPI
```
```
● DevExpress Scheduler
```
La vista può operare in due modalità:


```
● calendario
```
```
● lista
```
**4.2 Dati caricati**

Il metodo:

AssegnazioneTurnoAPI.getGlobalShift()

invoca:

GET /api/concrete-shifts/

Il parsing avviene in parseAllocatedShifts(), che:

```
● costruisce oggetti AssignedShift
```
```
● popola:
```
```
○ utenti_guardia
```
```
○ utenti_reperibili
```
```
○ utenti_rimossi
```
```
● assegna lo stato (Complete, Incomplete, Infeasible)
```
**4.3 Editing e vincoli**

Le modifiche ai turni avvengono tramite:

AssegnazioneTurnoAPI.aggiornaAssegnazioneTurno()

che invia:

PUT /api/concrete-shifts/


In caso di violazioni:

```
● il backend restituisce una lista di messaggi
```
```
● la UI li visualizza tramite ViolationLog
```
```
● l’assegnazione precedente viene ripristinata
```
Questo flusso dimostra che:

```
● la validazione è server-side
```
```
● la UI è reattiva ma non predittiva
```

## Microtask 4 — Aggiungere il tracciamento dei log del flusso backend per generazione/rigenerazione

**Obiettivo**

Introdurre log strutturati e correlabili per il flusso backend di generazione e rigenerazione
dello schedulo, con una correlazione unica per request e con copertura dei passaggi chiave
di orchestrazione (caricamento dati, vincoli/priorità, build, persistenza).

**Cosa è stato implementato**

```
● Filter di correlazione richieste con header primario X-Request-Id (fallback X-Correlation-Id)
● Inserimento requestId in MDC e echo di X-Request-Id nelle response
● Log strutturati nei REST entrypoint generation/regeneration con eventi start/success/fail
● Log strutturati nel SchedulerController per i passaggi chiave della pipeline
● Campi di fase per distinguere i data load in rigenerazione
● In rigenerazione, log di successo con originalPlanId e newPlanId (quando disponibile)
```

**Endpoint strumentati**

```
● POST /api/schedule/generation
● POST /api/schedule/regeneration/id={id}
```

**Nomi eventi introdotti**

```
● plan_generate_start / plan_generate_success / plan_generate_failed
● plan_regenerate_start / plan_regenerate_success / plan_regenerate_failed
● plan_<mode>_concrete_shifts_built
● plan_<mode>_data_loaded (con phase = schedule_lookup | priorities_loaded in rigenerazione)
● plan_<mode>_builder_initialized
● plan_<mode>_constraints_priorities_ready
● plan_<mode>_schedule_built
● plan_<mode>_schedule_saved
● plan_<mode>_priorities_saved
● plan_<mode>_persisted
● plan_<mode>_priorities_restored
● plan_<mode>_removed / plan_<mode>_remove_failed
● plan_<mode>_start_rejected
● plan_<mode>_failed
```

**Campi log principali**

```
● event (nome evento)
● requestId (da X-Request-Id o generato)
● mode = generate | regenerate
● durationMs (su step e eventi finali)
● planId, originalPlanId, newPlanId (quando applicabile)
● phase (solo per data_loaded in rigenerazione)
● counts: shiftsCount, concreteShiftsCount, constraintsCount, doctorsCount,
  holidaysCount, doctorHolidaysCount, prioritiesCount, snapshotCount,
  scocciaturaCount, savedPrioritiesCount, violatedConstraintsCount
● errorType / errorCode (solo in failure)
```

**Tracce di esempio e riproduzione**

```
curl -X POST "http://localhost:8080/api/schedule/generation" \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -H "X-Request-Id: req-gen-001" \
  -d '{
        "initialDay": 1,
        "initialMonth": 2,
        "initialYear": 2026,
        "finalDay": 7,
        "finalMonth": 2,
        "finalYear": 2026
      }'
```

```
event=plan_generate_start requestId=req-gen-001 mode=generate startDate=2026-02-01 endDate=2026-02-07
event=plan_generate_concrete_shifts_built requestId=req-gen-001 mode=generate durationMs=12 shiftsCount=6 concreteShiftsCount=42
event=plan_generate_data_loaded requestId=req-gen-001 mode=generate durationMs=24 shiftsCount=6 concreteShiftsCount=42 constraintsCount=5 doctorsCount=20 holidaysCount=3 doctorHolidaysCount=10 prioritiesCount=20 snapshotCount=20 scocciaturaCount=4
event=plan_generate_constraints_priorities_ready requestId=req-gen-001 mode=generate durationMs=2 constraintsCount=5 scocciaturaCount=4 prioritiesCount=20
event=plan_generate_schedule_built requestId=req-gen-001 mode=generate durationMs=180 concreteShiftsCount=42 violatedConstraintsCount=0
event=plan_generate_persisted requestId=req-gen-001 mode=generate durationMs=18 planId=101 savedPrioritiesCount=20
event=plan_generate_success requestId=req-gen-001 mode=generate durationMs=220 planId=101 violatedConstraintsCount=0 result=accepted
```

```
curl -X POST "http://localhost:8080/api/schedule/regeneration/id=101" \
  -H "Authorization: Bearer <JWT>" \
  -H "X-Request-Id: req-reg-001"
```

```
event=plan_regenerate_start requestId=req-reg-001 mode=regenerate planId=101
event=plan_regenerate_data_loaded requestId=req-reg-001 mode=regenerate durationMs=10 planId=101 phase=schedule_lookup result=ok
event=plan_regenerate_data_loaded requestId=req-reg-001 mode=regenerate durationMs=21 planId=101 phase=priorities_loaded prioritiesCount=20 snapshotCount=20
event=plan_regenerate_priorities_restored requestId=req-reg-001 mode=regenerate durationMs=3 planId=101 prioritiesCount=20
event=plan_regenerate_removed requestId=req-reg-001 mode=regenerate durationMs=15 planId=101
event=plan_regenerate_concrete_shifts_built requestId=req-reg-001 mode=regenerate durationMs=11 shiftsCount=6 concreteShiftsCount=42
event=plan_regenerate_data_loaded requestId=req-reg-001 mode=regenerate durationMs=31 shiftsCount=6 concreteShiftsCount=42 constraintsCount=5 doctorsCount=20 holidaysCount=3 doctorHolidaysCount=10 prioritiesCount=20 snapshotCount=20 scocciaturaCount=4
event=plan_regenerate_schedule_built requestId=req-reg-001 mode=regenerate durationMs=170 concreteShiftsCount=42 violatedConstraintsCount=0
event=plan_regenerate_persisted requestId=req-reg-001 mode=regenerate durationMs=18 planId=102 savedPrioritiesCount=20
event=plan_regenerate_success requestId=req-reg-001 mode=regenerate durationMs=240 originalPlanId=101 newPlanId=102 result=accepted
```

**Note / limitazioni**

```
● I log non includono payload o dati personali; vengono registrati solo ID e conteggi aggregati
● L’header X-Request-Id viene sempre ri-echo nella response
```

**Testing non eseguito (non richiesto)**

## **Microtask 5 — Aggiungere regression tests per gli endpoint di generazione schedulo**

**Obiettivo**

Creare una suite di test di regressione (Unit e Integration) per congelare il comportamento attuale degli endpoint di generazione, rigenerazione e gestione degli scheduli. Questo assicura che l'introduzione delle logiche AI nelle prossime storie non alteri il contratto esistente o la business logic critica (es. controlli sulle date, gestione priorità, deduplica).

**Cosa è stato implementato**

Sono state create/estese due classi di test:
1.  **`SchedulerControllerTest.java` (Unit Test)**: Isola la logica di controllo usando Mockito per i DAO. Copre tutti i rami decisionali di creazione, rimozione e rigenerazione.
2.  **`ScheduleTests.java` (Integration Test)**: Verifica il comportamento end-to-end con database H2, testando la persistenza reale, i vincoli di integrità e il ripristino delle priorità (UFFA).

**Dettaglio dei Test di Regressione (Unitari)**

I test unitari in `SchedulerControllerTest` sono organizzati per categoria funzionale:

* **Recupero Scheduli**
    * `getAllSchedulesWithDates_ReturnsDTOs`: Verifica il mapping corretto verso `ShowScheduleToPlannerDTO` usato dalla UI.
    * `readSchedules_ReturnsDTOs`: Verifica il recupero standard per l'amministrazione.
    * `readIllegalSchedules_ReturnsDTOs`: Assicura che gli scheduli marcati come illegali vengano filtrati correttamente.

* **Logica di Creazione**
    * `createSchedule_Success`: Valida il flusso felice di creazione per date future.
    * `createSchedule_InitialPastDate_ReturnsNull`: Verifica il blocco della creazione di scheduli nel passato (se è il primo).
    * `createSchedule_OverlapRequest_ReturnsNull`: Verifica la prevenzione di scheduli con range date identici a quelli esistenti.

* **Rimozione Schedulo**
    * `removeSchedule_PastSchedule_ReturnsFalse`: Garantisce che non si possano cancellare scheduli passati (storico).
    * `removeSchedule_FutureSchedule_Success`: Conferma la possibilità di cancellare scheduli futuri, inclusa la pulizia delle priorità associate.

* **Rigenerazione**
    * `recreateSchedule_PastSchedule_ReturnsFalse`: Impedisce la rigenerazione di scheduli storici.
    * `recreateSchedule_NotFound_ReturnsFalse`: Gestisce graceful failure su ID inesistenti.

* **Gestione Turni Concreti**
    * `addConcreteShift_ShiftNotFound_ThrowsException`: Verifica la validazione dei template di turno in input.

**Dettaglio dei Test di Regressione (Integrazione)**

I test in `ScheduleTests` verificano l'interazione con il layer di persistenza:

* **Validità Range Date (`createScheduleValidTest`)**: Test parametrico per confermare la creazione su range validi (futuro e presente).
* **Deduplica Reale (`testCreateScheduleDuplicateRangeFails`)**: Verifica che il DB/Controller rifiuti effettivamente duplicati esatti.
* **Ripristino Priorità (`testRecreateScheduleRestoresPrioritiesFlow`)**: Test critico per l'algoritmo UFFA. Verifica che, rigenerando uno schedulo, le priorità "spese" dai medici vengano resettate ai valori dello snapshot originale, garantendo equità.
* **Gestione Scheduli Illegali (`readIllegalScheduleTest`)**: Simula un flusso completo in cui un vincolo viene violato, l'eccezione catturata, lo schedulo salvato parzialmente e poi recuperato correttamente come "illegale".
* **Robustezza Inserimento Manuale (`testAddConcreteShiftDoctorCollision_DoesntThrowException`)**: Verifica che l'inserimento manuale (`addConcreteShift`) sia permissivo rispetto a collisioni di ruolo (stesso medico guardia+reperibile), mantenendo il comportamento attuale (nessuna eccezione lanciata).

**Note / Limitazioni**

* I test coprono la logica attuale "as-is", inclusi comportamenti potenzialmente migliorabili (es. collisioni manuali permesse) che non devono cambiare implicitamente durante il refactoring AI.
* I test di integrazione richiedono il seed dei dati (medici, servizi, task) eseguito nel `setUp()` per simulare un tenant valido.

## Microtask 6 - Document baseline flow in code comments + README notes
Aggiunto README in docs/AI_powered_rescheduling/sprint_4/README.md e javadoc nel codice.
