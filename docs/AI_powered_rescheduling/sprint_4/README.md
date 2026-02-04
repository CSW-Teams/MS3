# Documentazione Tecnica del Modulo di Scheduling (Sprint 4)

Questo documento consolida i riferimenti alla documentazione tecnica di base relativa al modulo di scheduling, focalizzandosi sugli aspetti analizzati nei Microtask 1.1, 1.2 e 1.3. L'obiettivo è fornire un punto di accesso rapido per sviluppatori e analisti che desiderano comprendere il funzionamento del sistema di generazione e gestione degli schedule.

---

## 1. Microtask 1.1: Backend - Scheduling Flow (Baseline)

Descrizione dettagliata del flusso di generazione e rigenerazione degli schedule, dei componenti principali e delle responsabilità del backend.

-   **Analisi Completa:** [roadmap_sprint_4.md#story-1---microtask-11--backend](docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md#story-1---microtask-11--backend)
-   **Punti di Ingresso REST:**
    -   `POST /schedule/generation`: [ScheduleRestEndpoint.java#createSchedule()](src/main/java/org/cswteams/ms3/rest/ScheduleRestEndpoint.java#L47)
    -   `POST /schedule/regeneration/{id}`: [ScheduleRestEndpoint.java#recreateSchedule()](src/main/java/org/cswteams/ms3/rest/ScheduleRestEndpoint.java#L98)
    -   `GET /schedule/`, `GET /schedule/dates/`, `GET /schedule/illegals`: [ScheduleRestEndpoint.java](src/main/java/org/cswteams/ms3/rest/ScheduleRestEndpoint.java)
    -   `DELETE /schedule/{id}`: [ScheduleRestEndpoint.java#deleteSchedule()](src/main/java/org/cswteams/ms3/rest/ScheduleRestEndpoint.java#L231)
-   **Orchestratore Applicativo:** [SchedulerController.java](src/main/java/org/cswteams/ms3/control/scheduler/SchedulerController.java)
    -   `createSchedule(startDate, endDate)` (proxy): [SchedulerController.java#createSchedule(LocalDate, LocalDate)](src/main/java/org/cswteams/ms3/control/scheduler/SchedulerController.java#L69)
    -   `createSchedule(startDate, endDate, ..., snapshot)` (full): [SchedulerController.java#createSchedule(LocalDate, LocalDate, List, List)](src/main/java/org/cswteams/ms3/control/scheduler/SchedulerController.java#L80)
    -   `recreateSchedule(id)`: [SchedulerController.java#recreateSchedule(long)](src/main/java/org/cswteams/ms3/control/scheduler/SchedulerController.java#L214)
-   **Motore di Generazione:** [ScheduleBuilder.java](src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java)
    -   `build()`: [ScheduleBuilder.java#build()](src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java#L169)

---

## 2. Microtask 1.2: Vincoli e Pipeline Priorità (Baseline)

Analisi del comportamento effettivo del sistema di scheduling riguardo vincoli e priorità ("uffa"/"scocciatura").

-   **Analisi Completa:** [roadmap_sprint_4.md#story-1---microtask-12--vincoli-e-pipeline-priorità-baseline](docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md#story-1---microtask-12--vincoli-e-pipeline-priorità-baseline)
-   **Contesto per i Vincoli:** [ContextConstraint.java](src/main/java/org/cswteams/ms3/entity/constraint/ContextConstraint.java)
-   **Catalogo Vincoli Attivi:**
    -   `ConstraintUbiquita`: [ConstraintUbiquita.java](src/main/java/org/cswteams/ms3/entity/constraint/ConstraintUbiquita.java)
    -   `ConstraintMaxOrePeriodo`: [ConstraintMaxOrePeriodo.java](src/main/java/org/cswteams/ms3/entity/constraint/ConstraintMaxOrePeriodo.java)
    -   `ConstraintMaxPeriodoConsecutivo`: [ConstraintMaxPeriodoConsecutivo.java](src/main/java/org/cswteams/ms3/entity/constraint/ConstraintMaxPeriodoConsecutivo.java)
    -   `ConstraintTurniContigui`: [ConstraintTurniContigui.java](src/main/java/org/cswteams/ms3/entity/constraint/ConstraintTurniContigui.java)
    -   `ConstraintHoliday`: [ConstraintHoliday.java](src/main/main/java/org/cswteams/ms3/entity/constraint/ConstraintHoliday.java)
    -   `ConstraintNumeroDiRuoloTurno`: [ConstraintNumeroDiRuoloTurno.java](src/main/java/org/cswteams/ms3/entity/constraint/ConstraintNumeroDiRuoloTurno.java)
    -   `AdditionalConstraint` (placeholder): [AdditionalConstraint.java](src/main/java/org/cswteams/ms3/entity/constraint/AdditionalConstraint.java)
-   **Pipeline Priorità (UFFA/Scocciatura):**
    -   `DoctorUffaPriority`: [DoctorUffaPriority.java](src/main/java/org/cswteams/ms3/entity/DoctorUffaPriority.java)
    -   `ControllerScocciatura`: [ControllerScocciatura.java](src/main/java/org/cswteams/ms3/control/scocciatura/ControllerScocciatura.java)
    -   `ScocciaturaAssegnazioneUtente`: [ScocciaturaAssegnazioneUtente.java](src/main/java/org/cswteams/ms3/entity/scocciature/ScocciaturaAssegnazioneUtente.java)
    -   `ScocciaturaDesiderata`: [ScocciaturaDesiderata.java](src/main/java/org/cswteams/ms3/entity/scocciature/ScocciaturaDesiderata.java)
    -   `ScocciaturaVacanza`: [ScocciaturaVacanza.java](src/main/java/org/cswteams.ms3/entity/scocciature/ScocciaturaVacanza.java)
    -   File di configurazione: [priority.properties](src/main/resources/priority.properties)

---

## 3. Microtask 1.3: Analisi Superfici UI per la Schedulazione (Planner)

Dettaglio delle interazioni UI ↔ backend e componenti React coinvolti nella schedulazione lato Planner.

-   **Analisi Completa:** [roadmap_sprint_4.md#story-1---microtask-13-analisi-delle-superfici-ui-per-la-schedulazione-planner](docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md#story-1---microtask-13-analisi-delle-superfici-ui-per-la-schedulazione-planner)
-   **Componenti Frontend:**
    -   `SchedulerGeneratorView`
    -   `TemporaryDrawerSchedulo` (o `BottomViewAggiungiSchedulazione`)
-   **API Frontend:** `AssegnazioneTurnoAPI.postGenerationSchedule()`, `ScheduleAPI` (`getSchedulazini()`, `deleteSchedule(id)`, `rigeneraSchedule(id)`), `AssegnazioneTurnoAPI.getGlobalShift()`, `AssegnazioneTurnoAPI.aggiornaAssegnazioneTurno()`
-   **Endpoint REST:** (vedi Microtask 1.1 - Punti di Ingresso REST)
-   **Gestione del Risultato:** [ScheduleRestEndpoint.java](src/main/java/org/cswteams/ms3/rest/ScheduleRestEndpoint.java) (gestione codici HTTP 202, 206, 406)
-   **Regole UI Codificate:** Logica di abilitazione della rigenerazione solo per l'ultimo schedule.
-   **Visualizzazione e Modifica Turni:** Componenti `ScheduleView`, `DevExpress Scheduler`, gestione `PUT /api/concrete-shifts/` e `ViolationLog`.
