package org.cswteams.ms3.rest;

import org.cswteams.ms3.ai.comparison.dto.AiScheduleComparisonResponseDto;
import org.cswteams.ms3.ai.comparison.dto.AiScheduleSelectionRequestDto;
import org.cswteams.ms3.ai.orchestration.AiScheduleGenerationOrchestrationService;
import org.cswteams.ms3.control.scheduler.ISchedulerController;
import org.cswteams.ms3.dto.ScheduleGenerationDTO;
import org.cswteams.ms3.dto.ScheduleDTO;
import org.cswteams.ms3.dto.showscheduletoplanner.ShowScheduleToPlannerDTO;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.exception.UnableToBuildScheduleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Questo è l'entry point REST per tutte le operazioni di scheduling (generazione, rigenerazione, eliminazione e lettura).
 * Agisce come un livello sottile, validando gli input di base e delegando tutta la logica di business a {@link ISchedulerController}.
 *
 * Durante le operazioni di generazione e rigenerazione, restituisce codici di stato HTTP all'interfaccia utente
 * piuttosto che l'intero payload dello schedule, come descritto nell'analisi dell'interfaccia utente (Microtask 1.3).
 *
 * Per maggiori dettagli sul flusso di schedulazione e i punti di contatto con la UI, si veda:
 * @see docs/scheduling_flow/README.md
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-13-analisi-delle-superfici-ui-per-la-schedulazione-planner
 */
@RestController
@RequestMapping("/schedule/")
public class ScheduleRestEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleRestEndpoint.class);
    private static final String MODE_KEY = "planMode";
    private static final String NEW_PLAN_ID_KEY = "newPlanId";

    @Autowired
    private ISchedulerController schedulerController;

    @Autowired
    private AiScheduleGenerationOrchestrationService aiScheduleGenerationOrchestrationService;

    /**
     * Questo metodo è invocato dal frontend per richiedere la generazione di un nuovo schedule di turni
     * nell'intervallo di date passato come parametro.
     *
     * Flusso UI-Backend (Microtask 1.3 - Generazione dello schedulo):
     * 1. Il Planner apre la drawer tramite il pulsante "Create schedule".
     * 2. Il componente `TemporaryDrawerSchedulo` (o `BottomViewAggiungiSchedulazione`) raccoglie `dataInizio` e `dataFine`.
     * 3. Alla conferma, viene invocato `AssegnazioneTurnoAPI.postGenerationSchedule(dataInizio, dataFine)`.
     * 4. Il metodo costruisce un payload e invia la richiesta a `POST /api/schedule/generation`.
     *
     * Gestione del risultato (Microtask 1.3):
     * Il frontend non riceve lo schedulo generato, ma interpreta esclusivamente lo status HTTP:
     * - `202 ACCEPTED`: Schedulo creato correttamente.
     * - `206 PARTIAL_CONTENT`: Schedulo incompleto (con vincoli violati).
     * - `406 NOT_ACCEPTABLE`: Schedulo duplicato o non generabile.
     * - `400 BAD_REQUEST`: Errore generico (es. parametri non validi).
     *
     * @param gs DTO contenente le date di inizio e fine per la generazione dello schedule.
     * @return ResponseEntity con lo stato HTTP che indica l'esito dell'operazione.
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-13-analisi-delle-superfici-ui-per-la-schedulazione-planner
     */
    @PreAuthorize("hasAnyRole('PLANNER')")
    @RequestMapping(method = RequestMethod.POST, path = "generation")
    public ResponseEntity<?> createSchedule(@RequestBody() ScheduleGenerationDTO gs) {
        long startTime = System.currentTimeMillis();
        MDC.put(MODE_KEY, "generate");
        Map<String, Object> startFields = new LinkedHashMap<>();
        startFields.put("startDate", gs != null ? gs.getStartDate() : null);
        startFields.put("endDate", gs != null ? gs.getEndDate() : null);
        logEvent("plan_generate_start", "generate", startFields);
        try {
            if (gs != null) {
                //Only the requests with admissible dates will be considered.
                if(!gs.getEndDate().isBefore(gs.getStartDate())){

                    //The request is passed to the controller.
                    Schedule schedule = schedulerController.createSchedule(gs.getStartDate(),gs.getEndDate());
                    if(schedule == null) {
                        logEvent("plan_generate_failed", "generate", Map.of(
                                "durationMs", System.currentTimeMillis() - startTime,
                                "errorType", "NOT_ACCEPTABLE"
                        ));
                        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                    }
                    if(!schedule.getViolatedConstraints().isEmpty()) {
                        logEvent("plan_generate_success", "generate", Map.of(
                                "durationMs", System.currentTimeMillis() - startTime,
                                "planId", schedule.getId(),
                                "violatedConstraintsCount", schedule.getViolatedConstraints().size(),
                                "result", "partial"
                        ));
                        return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
                    } else {
                        logEvent("plan_generate_success", "generate", Map.of(
                                "durationMs", System.currentTimeMillis() - startTime,
                                "planId", schedule.getId(),
                                "violatedConstraintsCount", schedule.getViolatedConstraints().size(),
                                "result", "accepted"
                        ));
                        return new ResponseEntity<>(HttpStatus.ACCEPTED);
                    }
                }
            }
            logEvent("plan_generate_failed", "generate", Map.of(
                    "durationMs", System.currentTimeMillis() - startTime,
                    "errorType", "BAD_REQUEST"
            ));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } finally {
            MDC.remove(MODE_KEY);
        }
    }

    /**
     * Endpoint dedicato alla generazione di schedulazioni con orchestrazione AI.
     * Restituisce il confronto tra la schedulazione standard e le tre varianti AI (empatica, efficiente, bilanciata)
     * includendo metriche e payload JSON per ciascun candidato.
     *
     * @param gs DTO contenente le date di inizio e fine per la generazione dello schedule.
     * @return ResponseEntity con il payload di confronto o errore di validazione.
     */
    @PreAuthorize("hasAnyRole('PLANNER')")
    @RequestMapping(method = RequestMethod.POST, path = "generation/ai")
    public ResponseEntity<?> createScheduleWithAi(@RequestBody() ScheduleGenerationDTO gs) {
        if (gs == null || gs.getEndDate().isBefore(gs.getStartDate())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        AiScheduleComparisonResponseDto response = aiScheduleGenerationOrchestrationService.generateScheduleComparison(
                gs.getStartDate(),
                gs.getEndDate()
        );
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint dedicato alla selezione finale della schedulazione preferita tra i candidati.
     * Accetta l'identificativo/label del candidato scelto e persiste solo quello.
     *
     * @param selection DTO con l'id/label del candidato selezionato.
     * @return ResponseEntity con lo stato HTTP che indica l'esito dell'operazione.
     */
    @PreAuthorize("hasAnyRole('PLANNER')")
    @RequestMapping(method = RequestMethod.POST, path = "selection")
    public ResponseEntity<?> selectScheduleCandidate(@RequestBody AiScheduleSelectionRequestDto selection) {
        if (selection == null || selection.getCandidateId() == null || selection.getCandidateId().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        AiScheduleGenerationOrchestrationService.SelectionResult result =
                aiScheduleGenerationOrchestrationService.persistSelectedCandidate(selection.getCandidateId());
        if (result.getStatus() == AiScheduleGenerationOrchestrationService.SelectionResult.Status.PERSISTED) {
            return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
        }
        if (result.getStatus() == AiScheduleGenerationOrchestrationService.SelectionResult.Status.DUPLICATE_RANGE) {
            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
        }
        if (result.getStatus() == AiScheduleGenerationOrchestrationService.SelectionResult.Status.NO_ACTIVE_COMPARISON
                || result.getStatus() == AiScheduleGenerationOrchestrationService.SelectionResult.Status.CANDIDATE_NOT_FOUND) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    /**
     * Questo metodo è invocato dal frontend per richiedere la rigenerazione di uno schedule di turni esistente.
     *
     * Regole UI codificate (Microtask 1.3):
     * Nel codice frontend è esplicitamente codificata la regola: "Solo l’ultimo schedulo può essere rigenerato."
     * Questo vincolo anticipa un concetto di "schedulo corrente" e sarà rilevante per il confronto multi-schedulo AI.
     *
     * Flusso UI-Backend (Microtask 1.3):
     * 1. Il client invia `POST /api/schedule/regeneration/id={id}`.
     * 2. Il REST chiama `schedulerController.recreateSchedule(id)`.
     *
     * Gestione del risultato (Microtask 1.3):
     * Il frontend interpreta lo status HTTP:
     * - `202 ACCEPTED`: Se la rigenerazione ha avuto successo.
     * - `417 EXPECTATION_FAILED`: Se la rigenerazione fallisce.
     * - `400 BAD_REQUEST`: Su eccezione o parametri errati.
     *
     * @param id L'ID dello schedule da rigenerare.
     * @return ResponseEntity con lo stato HTTP che indica l'esito dell'operazione.
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-13-analisi-delle-superfici-ui-per-la-schedulazione-planner
     */
    @PreAuthorize("hasAnyRole('PLANNER')")
    @RequestMapping(method = RequestMethod.POST, path = "regeneration/id={id}")
    public ResponseEntity<?> recreateSchedule(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        MDC.put(MODE_KEY, "regenerate");
        Map<String, Object> regenerateStartFields = new LinkedHashMap<>();
        regenerateStartFields.put("planId", id);
        logEvent("plan_regenerate_start", "regenerate", regenerateStartFields);
        try {
            if (id != null) {
                try {
                    if(schedulerController.recreateSchedule(id)) {
                        String newPlanId = MDC.get(NEW_PLAN_ID_KEY);
                        logEvent("plan_regenerate_success", "regenerate", Map.of(
                                "durationMs", System.currentTimeMillis() - startTime,
                                "originalPlanId", id,
                                "newPlanId", newPlanId != null ? newPlanId : "unknown",
                                "result", "accepted"
                        ));
                        return new ResponseEntity<>(HttpStatus.ACCEPTED);
                    } else {
                        logEvent("plan_regenerate_failed", "regenerate", Map.of(
                                "durationMs", System.currentTimeMillis() - startTime,
                                "planId", id,
                                "errorType", "EXPECTATION_FAILED"
                        ));
                        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
                    }
                } catch (UnableToBuildScheduleException e) {
                    logEvent("plan_regenerate_failed", "regenerate", Map.of(
                            "durationMs", System.currentTimeMillis() - startTime,
                            "planId", id,
                            "errorType", e.getClass().getSimpleName(),
                            "errorCode", "UNABLE_TO_BUILD_SCHEDULE"
                    ));
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }
            logEvent("plan_regenerate_failed", "regenerate", Map.of(
                    "durationMs", System.currentTimeMillis() - startTime,
                    "errorType", "BAD_REQUEST"
            ));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } finally {
            MDC.remove(NEW_PLAN_ID_KEY);
            MDC.remove(MODE_KEY);
        }
    }

    /**
     * Questo metodo è invocato per recuperare tutti gli schedule di turni esistenti.
     * Utilizzato dalla UI per popolare la lista degli schedule disponibili, ad esempio nella `SchedulerGeneratorView`.
     *
     * @return ResponseEntity contenente una lista di {@link ScheduleDTO} e lo stato HTTP.
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-13-analisi-delle-superfici-ui-per-la-schedulazione-planner
     */
    @PreAuthorize("hasAnyRole('PLANNER')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> readSchedules()  {

        List<ScheduleDTO> set = schedulerController.readSchedules();
        return new ResponseEntity<>(set, HttpStatus.FOUND);

    }


    /**
     * Request send by the client when we want to show only the schedules to the planner
     * @return FOUND if the query had success, NOT FOUND if the query returned 0, ERROR if something went wrong
     */
    @PreAuthorize("hasAnyRole('DOCTOR', 'PLANNER')")
    @RequestMapping(method = RequestMethod.GET,path = "/dates/")
    public ResponseEntity<?> getAllSchedulesWithDates()  {
        Set<ShowScheduleToPlannerDTO> showScheduleToPlannerDTOSet;
        try {
            showScheduleToPlannerDTOSet= schedulerController.getAllSchedulesWithDates();
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(schedulerController == null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else if(showScheduleToPlannerDTOSet.isEmpty()){
            return new ResponseEntity<>(showScheduleToPlannerDTOSet, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(showScheduleToPlannerDTOSet, HttpStatus.FOUND);

    }

    /**
     * Questo metodo è invocato per recuperare gli schedule di turni che contengono violazioni.
     * La UI può usarlo per mostrare all'utente gli schedule "parziali" o "illegali" che sono stati generati.
     *
     * @return ResponseEntity contenente una lista di {@link ScheduleDTO} che rappresentano schedule illegali e lo stato HTTP.
     */
    @PreAuthorize("hasAnyRole('DOCTOR', 'PLANNER', 'CONFIGURATOR')")
    @RequestMapping(method = RequestMethod.GET,path = "illegals")
    public ResponseEntity<?> readIllegalSchedules()  {

        List<ScheduleDTO> set = schedulerController.readIllegalSchedules();
        return new ResponseEntity<>(set, HttpStatus.FOUND);

    }

    /**
     * Questo metodo è invocato per eliminare uno schedule di turni esistente.
     *
     * Flusso UI (Microtask 1.3 - Eliminazione schedulo):
     * 1. Viene mostrata una loading overlay.
     * 2. Viene inviata una richiesta DELETE a questo endpoint.
     * 3. La UI reagisce in base allo status HTTP (200 OK, 400 BAD_REQUEST, 417 EXPECTATION_FAILED).
     * 4. La lista degli schedule viene ricaricata tramite `componentDidMount()`.
     *
     * Non sono presenti nella UI funzionalità di undo o soft delete.
     *
     * @param id L'ID dello schedule da eliminare.
     * @return ResponseEntity con lo stato HTTP che indica l'esito dell'operazione.
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-13-analisi-delle-superfici-ui-per-la-schedulazione-planner
     */
    @PreAuthorize("hasAnyRole('PLANNER')")
    @RequestMapping(method = RequestMethod.DELETE, path = "id={id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id)  {

        if (id != null ) {
            //It is not possible to delete a schedule in the past. This check is made by the controller.
            if(schedulerController.removeSchedule(id))
                return new ResponseEntity<>(HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private void logEvent(String event, String mode, Map<String, Object> fields) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("event", event);
        data.put("requestId", getRequestId());
        data.put("mode", mode);
        data.putAll(fields);
        logger.info(formatLogMessage(data));
    }

    private String getRequestId() {
        String requestId = MDC.get("requestId");
        return requestId != null ? requestId : "unknown";
    }

    private String formatLogMessage(Map<String, Object> fields) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(entry.getKey()).append('=').append(formatValue(entry.getValue()));
        }
        return builder.toString();
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        String text = value.toString();
        if (text.contains(" ") || text.contains("=")) {
            return '"' + text.replace("\"", "\\\"") + '"';
        }
        return text;
    }


}
