# Final Quality Gate: Comment Completeness and Readability (Post-`5c533ad4`)

## Purpose
This quality gate defines the final review needed to confirm that all code introduced or modified after commit `5c533ad4` has complete, clear, and accurate comments.

It is reusable for future comment hardening cycles by changing only the baseline commit and the scope inventory.

## Inputs Required Before Review
- **Baseline commit**: `5c533ad4` (or another baseline for future cycles).
- **Scope inventory**: list of post-baseline files included in the review.
- **Intentional exclusions list**: files that are not part of the cycle, with rationale.

### Baseline refresh references
- For baseline `c72ee7a7`, use inventory: `commenting_scope_post_c72ee7a7.csv`.
- Deterministic snapshot metadata: `docs/commenting_scope_post_c72ee7a7_snapshot.md`.

---

## Pass/Fail Checklist (File-Level)
Evaluate every in-scope file against each criterion.

### Criterion 1 — Intent Comments Present Where Needed
- **Pass** if each class/component/file with non-trivial responsibility includes at least one clear intent or summary comment where needed.
- **Fail** if a file needs intent context but has no class/component intent comment.

### Criterion 2 — Complex Methods Include Rationale
- **Pass** if methods/functions with complex branches, constraints, or trade-offs include rationale comments explaining why the logic exists.
- **Fail** if complex logic has comments that only restate code behavior or has no rationale.

### Criterion 3 — Language Is Simple English
- **Pass** if comments are in simple English, with short, direct wording understandable by non-specialist reviewers.
- **Fail** if comments contain unclear jargon, mixed languages, or unnecessarily complex phrasing.

### Criterion 4 — No Stale or Contradictory Comments
- **Pass** if comments are aligned with current behavior and do not conflict with code or other comments.
- **Fail** if comments describe removed behavior, outdated assumptions, or contradictory statements.

### File Decision Rule
- **File PASS**: all four criteria pass.
- **File FAIL**: one or more criteria fail.

### Gate Decision Rule
- **Gate PASS**: all reviewed in-scope files pass.
- **Gate FAIL**: at least one reviewed in-scope file fails.

---

## Reviewer Workflow

### Step 1 — File-by-File Review Against Scope Inventory
1. Start from the approved scope inventory of post-baseline files.
2. Review each file sequentially and record pass/fail per criterion.
3. Add concise evidence notes for each fail to enable targeted fixes.

### Step 2 — Module Owner Spot-Check
Perform spot-checks after the file-by-file pass:
- **Backend owner** reviews a sample of Java/Spring files.
- **Frontend owner** reviews a sample of React/JavaScript files.
- **DB owner** reviews a sample of SQL/migration files.

Spot-check objective: verify consistency of comment quality across modules, not only per-file checklist completion.

### Step 3 — Final Presentation-Readability Pass
Run a final readability pass focused on external presentation quality:
- Comments are understandable when read aloud in review/demo contexts.
- Comments explain intent and rationale clearly for mixed technical/non-technical audiences.
- No wording that could create confusion during stakeholder presentations.

---

## Required Final Report (Mandatory Output)
Every cycle must end with a report containing:

1. **Files reviewed**
   - Full list of in-scope files evaluated by the checklist.
2. **Files intentionally excluded**
   - Explicit list of excluded files with reason for exclusion.
3. **Open comment debt**
   - Any remaining comment issues not fixed in the cycle.
   - For each debt item: file, issue summary, risk, and planned follow-up.

If no debt remains, report `Open comment debt: none`.

---

## Reusable Template for Future Comment Hardening Cycles

Use this section as a copy/paste template.

### A) Cycle Header
- Baseline commit: `<commit_hash>`
- Review date: `<YYYY-MM-DD>`
- Reviewers: `<names/roles>`

### B) Scope Inputs
- Scope inventory source: `<path/link>`
- Included file count: `<n>`
- Excluded file count: `<n>`

### C) File Review Table
| File | Intent comments present | Complex rationale comments | Simple English | No stale/contradictory comments | File result | Notes |
|---|---|---|---|---|---|---|
| `<path>` | Pass/Fail | Pass/Fail | Pass/Fail | Pass/Fail | Pass/Fail | `<evidence>` |

### D) Spot-Check Log
- Backend spot-check: `<files sampled + result>`
- Frontend spot-check: `<files sampled + result>`
- DB spot-check: `<files sampled + result>`

### E) Presentation-Readability Outcome
- Result: `Pass/Fail`
- Notes: `<short rationale>`

### F) Final Gate Decision
- Gate decision: `PASS/FAIL`
- Blocking issues (if any): `<list>`

### G) Final Report Summary
- Files reviewed: `<list or link>`
- Files intentionally excluded: `<list + reasons>`
- Open comment debt: `<none or list with follow-up>`

<!-- LOCKED_CHECKLIST_START -->
## Official Locked Scope Checklist (post-`5c533ad4`)

- Baseline: `5c533ad4`
- Target: `HEAD` (`7462f86`)
- Inventory source: `commenting_scope_post_5c533ad4.csv`
- Total entries: **310**
- In scope (`reverted_only=no`): **297**
- Excluded (`reverted_only=yes`): **13**

This checklist is **frozen as official** for subsequent review/PR cycles tied to baseline `5c533ad4`.
Update it only via a deliberate re-baselining or explicit scope-refresh commit.

### Exclusion rule
- `reverted_only=yes` => excluded from active review (`in_scope=no`).
- `reverted_only=no` => included in active review (`in_scope=yes`).

### In-scope entries (`in_scope=yes`)
- `.env`
- `AGENTS.md`
- `README.md`
- `commenting_scope_post_5c533ad4.csv`
- `docker-compose-debug.yml`
- `docker-compose.yml`
- `docs/AI_powered_rescheduling/ai_prompt_pipeline_extension_points.md`
- `docs/AI_powered_rescheduling/dual_layer_coverage_monitoring.md`
- `docs/AI_powered_rescheduling/sprint_4/README.md`
- `docs/AI_powered_rescheduling/sprint_4/ai_agent_comparison.md`
- `docs/AI_powered_rescheduling/sprint_4/ai_rescheduling_orchestration_flow.drawio`
- `docs/AI_powered_rescheduling/sprint_4/checkbox_roadmap_sprint_4.md`
- `docs/AI_powered_rescheduling/sprint_4/retrospective_inputs.md`
- `docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md`
- `docs/AI_powered_rescheduling/sprint_4/sprint_summary.md`
- `docs/AI_powered_rescheduling/sprint_4/stakeholder_slide_deck_outline.md`
- `docs/AI_powered_rescheduling/sprint_4/story_1.md`
- `docs/AI_powered_rescheduling/sprint_4/story_2.md`
- `docs/AI_powered_rescheduling/sprint_4/story_3.md`
- `docs/AI_powered_rescheduling/sprint_4/story_4.md`
- `docs/AI_powered_rescheduling/sprint_4/story_5.md`
- `docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md`
- `docs/GPT/codex-prompt-builder/PROJECT_INSTRUCTIONS.md`
- `docs/GPT/general/PROJECT_INSTRUCTIONS.md`
- `docs/codacy-manuale.md`
- `docs/commenting_guideline_post_5c533ad4.md`
- `docs/commenting_quality_gate_post_5c533ad4.md`
- `docs/data_generation/doctor_attributes_for_scheduling_as_is.md`
- `docs/scheduling/AI-Powered_scheduling_analysis.md`
- `docs/scheduling/AI_agents_architectural_analysis.md`
- `docs/scheduling/AI_agents_in-depth_analysis.md`
- `docs/scheduling/AI_agents_preliminar_analysis.md`
- `docs/scheduling/scheduling_as-is_analysis.md`
- `docs/scheduling_flow/ai_generation_selection_flow.md`
- `docs/security/2fa_totp_codex_implementation_prompts.md`
- `docs/security/2fa_totp_design.md`
- `docs/testing/db-bootstrap.md`
- `doctors_seed.json`
- `doctors_seed.sql`
- `doctors_seed.toon`
- `frontend/.env`
- `frontend/package.json`
- `frontend/src/API/AssegnazioneTurnoAPI.js`
- `frontend/src/API/LoginAPI.js`
- `frontend/src/API/LogoutAPI.js`
- `frontend/src/API/NotificationAPI.js`
- `frontend/src/API/ScheduleAPI.js`
- `frontend/src/API/ScheduleFeedbackAPI.js`
- `frontend/src/API/TwoFactorAPI.js`
- `frontend/src/components/common/AiScheduleComparisonModal.js`
- `frontend/src/components/common/AiScheduleSelectionConfirmationModal.js`
- `frontend/src/components/common/BottomViewAggiungiSchedulazione.js`
- `frontend/src/components/common/CustomAppointmentComponents.js`
- `frontend/src/components/common/GenerationLoadingModal.js`
- `frontend/src/components/common/GenerationStatusFeedback.js`
- `frontend/src/components/common/TurnstileWidget.js`
- `frontend/src/components/layout/MainFooter.js`
- `frontend/src/components/layout/MainNavbar/NavbarNav/UserActions.js`
- `frontend/src/data/sidebar-nav-items.js`
- `frontend/src/locales/en.json`
- `frontend/src/locales/it.json`
- `frontend/src/routes.js`
- `frontend/src/views/pianificatore/FeedbackManagementView.js`
- `frontend/src/views/pianificatore/ScheduleGeneratorView.js`
- `frontend/src/views/utente/LoginView.js`
- `frontend/src/views/utente/LoginView.test.js`
- `frontend/src/views/utente/TwoFactorEnrollmentView.js`
- `frontend/src/views/utente/TwoFactorEnrollmentView.test.js`
- `mvnw`
- `pom.xml`
- `src/main/java/org/cswteams/ms3/ai/broker/AgentBroker.java`
- `src/main/java/org/cswteams/ms3/ai/broker/AgentBrokerImpl.java`
- `src/main/java/org/cswteams/ms3/ai/broker/AgentProvider.java`
- `src/main/java/org/cswteams/ms3/ai/broker/AgentProviderAdapter.java`
- `src/main/java/org/cswteams/ms3/ai/broker/AiBrokerConfiguration.java`
- `src/main/java/org/cswteams/ms3/ai/broker/AiBrokerProperties.java`
- `src/main/java/org/cswteams/ms3/ai/broker/AiBrokerRequest.java`
- `src/main/java/org/cswteams/ms3/ai/broker/AiPromptTemplate.java`
- `src/main/java/org/cswteams/ms3/ai/broker/AiTokenBudgetGuardResult.java`
- `src/main/java/org/cswteams/ms3/ai/broker/AiTokenEstimator.java`
- `src/main/java/org/cswteams/ms3/ai/broker/AiTokenUsageTracker.java`
- `src/main/java/org/cswteams/ms3/ai/broker/GemmaAgentAdapter.java`
- `src/main/java/org/cswteams/ms3/ai/broker/Llama70bAgentAdapter.java`
- `src/main/java/org/cswteams/ms3/ai/broker/domain/AiAssignment.java`
- `src/main/java/org/cswteams/ms3/ai/broker/domain/AiMetadata.java`
- `src/main/java/org/cswteams/ms3/ai/broker/domain/AiMetrics.java`
- `src/main/java/org/cswteams/ms3/ai/broker/domain/AiScheduleResponse.java`
- `src/main/java/org/cswteams/ms3/ai/broker/domain/AiScheduleVariantsResponse.java`
- `src/main/java/org/cswteams/ms3/ai/broker/domain/AiStdDev.java`
- `src/main/java/org/cswteams/ms3/ai/broker/domain/AiUffaBalance.java`
- `src/main/java/org/cswteams/ms3/ai/broker/domain/AiUffaDelta.java`
- `src/main/java/org/cswteams/ms3/ai/broker/domain/AiUncoveredShift.java`
- `src/main/java/org/cswteams/ms3/ai/broker/mapper/AiScheduleResponseMapper.java`
- `src/main/java/org/cswteams/ms3/ai/comparison/domain/AiScheduleComparisonCandidate.java`
- `src/main/java/org/cswteams/ms3/ai/comparison/domain/AiScheduleDecisionOutcome.java`
- `src/main/java/org/cswteams/ms3/ai/comparison/domain/DecisionMetricValues.java`
- `src/main/java/org/cswteams/ms3/ai/comparison/domain/ScheduleCandidateType.java`
- `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleCandidateMetadataDto.java`
- `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleComparisonCandidateDto.java`
- `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleComparisonResponseDto.java`
- `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleDecisionMetricValuesDto.java`
- `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleDecisionMetricsDto.java`
- `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleDecisionOutcomeDto.java`
- `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleSelectionRequestDto.java`
- `src/main/java/org/cswteams/ms3/ai/comparison/mapper/AiScheduleComparisonMapper.java`
- `src/main/java/org/cswteams/ms3/ai/decision/AiScheduleCandidateMetrics.java`
- `src/main/java/org/cswteams/ms3/ai/decision/DecisionAlgorithmService.java`
- `src/main/java/org/cswteams/ms3/ai/decision/DecisionAlgorithmServiceImpl.java`
- `src/main/java/org/cswteams/ms3/ai/metrics/MetricAggregationUtils.java`
- `src/main/java/org/cswteams/ms3/ai/metrics/MetricNormalizationUtils.java`
- `src/main/java/org/cswteams/ms3/ai/metrics/UffaDeltaStats.java`
- `src/main/java/org/cswteams/ms3/ai/orchestration/AiActiveConstraintResolver.java`
- `src/main/java/org/cswteams/ms3/ai/orchestration/AiHardCoveragePromptBlockBuilder.java`
- `src/main/java/org/cswteams/ms3/ai/orchestration/AiReschedulingOrchestrationService.java`
- `src/main/java/org/cswteams/ms3/ai/orchestration/AiReschedulingToonRequest.java`
- `src/main/java/org/cswteams/ms3/ai/orchestration/AiRoleValidationScratchpadPromptBlockBuilder.java`
- `src/main/java/org/cswteams/ms3/ai/orchestration/AiScheduleGenerationOrchestrationService.java`
- `src/main/java/org/cswteams/ms3/ai/priority/PriorityDimension.java`
- `src/main/java/org/cswteams/ms3/ai/priority/PriorityScaleConfig.java`
- `src/main/java/org/cswteams/ms3/ai/priority/PriorityScaleProperties.java`
- `src/main/java/org/cswteams/ms3/ai/priority/PriorityScaleValidationException.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/AiScheduleJsonParser.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/AiScheduleSemanticValidator.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/AiUffaDeltaDeserializer.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/ValidationError.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/converter/AiScheduleConverterService.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiAssignmentDto.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiMetadataDto.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiMetricsDto.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiRoleCoveredDeserializer.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiRoleValidationScratchpadItemDto.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiScheduleResponseDto.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiScheduleVariantsResponseDto.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiStdDevDto.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiUffaBalanceDto.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiUffaDeltaDto.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiUncoveredShiftDto.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/exceptions/AiProtocolException.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/utils/AiStatus.java`
- `src/main/java/org/cswteams/ms3/ai/protocol/utils/AiUffaQueue.java`
- `src/main/java/org/cswteams/ms3/audit/AuditProperties.java`
- `src/main/java/org/cswteams/ms3/audit/selection/AuditRecorder.java`
- `src/main/java/org/cswteams/ms3/audit/selection/AuditSelection.java`
- `src/main/java/org/cswteams/ms3/audit/selection/AuditSelectionAspect.java`
- `src/main/java/org/cswteams/ms3/audit/selection/AuditableSelectionResult.java`
- `src/main/java/org/cswteams/ms3/audit/selection/AuditedSelectionResult.java`
- `src/main/java/org/cswteams/ms3/audit/selection/SelectionAuditEvent.java`
- `src/main/java/org/cswteams/ms3/audit/validation/ErrorCategory.java`
- `src/main/java/org/cswteams/ms3/audit/validation/MetricComputationResult.java`
- `src/main/java/org/cswteams/ms3/audit/validation/MetricComputationValidator.java`
- `src/main/java/org/cswteams/ms3/audit/validation/MetricValidationErrorResponse.java`
- `src/main/java/org/cswteams/ms3/audit/validation/MetricValidationException.java`
- `src/main/java/org/cswteams/ms3/audit/validation/MetricValidationExceptionHandler.java`
- `src/main/java/org/cswteams/ms3/audit/validation/ValidationViolation.java`
- `src/main/java/org/cswteams/ms3/config/AppConfig.java`
- `src/main/java/org/cswteams/ms3/config/ApplicationStartup.java`
- `src/main/java/org/cswteams/ms3/config/SchedulerConfig.java`
- `src/main/java/org/cswteams/ms3/config/multitenancy/SchemaSwitchingConnectionProviderPostgreSQL.java`
- `src/main/java/org/cswteams/ms3/config/multitenancy/SchemasInitializer.java`
- `src/main/java/org/cswteams/ms3/control/login/ILoginController.java`
- `src/main/java/org/cswteams/ms3/control/login/LoginController.java`
- `src/main/java/org/cswteams/ms3/control/logout/ExpiredTokensRemovalService.java`
- `src/main/java/org/cswteams/ms3/control/logout/ILogoutController.java`
- `src/main/java/org/cswteams/ms3/control/logout/JwtBlacklistService.java`
- `src/main/java/org/cswteams/ms3/control/logout/LogoutController.java`
- `src/main/java/org/cswteams/ms3/control/passwordChange/PasswordChange.java`
- `src/main/java/org/cswteams/ms3/control/scheduleFeedback/IScheduleFeedbackController.java`
- `src/main/java/org/cswteams/ms3/control/scheduleFeedback/ScheduleFeedbackController.java`
- `src/main/java/org/cswteams/ms3/control/scheduler/ISchedulerController.java`
- `src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java`
- `src/main/java/org/cswteams/ms3/control/scheduler/SchedulerController.java`
- `src/main/java/org/cswteams/ms3/control/scocciatura/ControllerScocciatura.java`
- `src/main/java/org/cswteams/ms3/control/toon/ToonActiveConstraint.java`
- `src/main/java/org/cswteams/ms3/control/toon/ToonBuilder.java`
- `src/main/java/org/cswteams/ms3/control/toon/ToonConstraintEntityType.java`
- `src/main/java/org/cswteams/ms3/control/toon/ToonConstraintType.java`
- `src/main/java/org/cswteams/ms3/control/toon/ToonFeedback.java`
- `src/main/java/org/cswteams/ms3/control/toon/ToonPseudonymizationMapper.java`
- `src/main/java/org/cswteams/ms3/control/toon/ToonPseudonymizationResult.java`
- `src/main/java/org/cswteams/ms3/control/toon/ToonRequestContext.java`
- `src/main/java/org/cswteams/ms3/control/toon/ToonValidationException.java`
- `src/main/java/org/cswteams/ms3/control/toon/ToonValidator.java`
- `src/main/java/org/cswteams/ms3/dao/BlacklistedTokenDAO.java`
- `src/main/java/org/cswteams/ms3/dao/ConcreteShiftDAO.java`
- `src/main/java/org/cswteams/ms3/dao/DoctorDAO.java`
- `src/main/java/org/cswteams/ms3/dao/DoctorHolidaysDAO.java`
- `src/main/java/org/cswteams/ms3/dao/DoctorUffaPriorityDAO.java`
- `src/main/java/org/cswteams/ms3/dao/RequestRemovalFromConcreteShiftDAO.java`
- `src/main/java/org/cswteams/ms3/dao/ScheduleFeedbackDAO.java`
- `src/main/java/org/cswteams/ms3/dao/SelectionAuditRecordRepository.java`
- `src/main/java/org/cswteams/ms3/dao/SystemUserDAO.java`
- `src/main/java/org/cswteams/ms3/dto/login/CustomUserDetails.java`
- `src/main/java/org/cswteams/ms3/dto/login/LoginRequestDTO.java`
- `src/main/java/org/cswteams/ms3/dto/login/LoginResponseDTO.java`
- `src/main/java/org/cswteams/ms3/dto/scheduleFeedback/ScheduleFeedbackDTO.java`
- `src/main/java/org/cswteams/ms3/entity/BlacklistedToken.java`
- `src/main/java/org/cswteams/ms3/entity/DoctorUffaPriority.java`
- `src/main/java/org/cswteams/ms3/entity/Schedule.java`
- `src/main/java/org/cswteams/ms3/entity/ScheduleFeedback.java`
- `src/main/java/org/cswteams/ms3/entity/SelectionAuditRecord.java`
- `src/main/java/org/cswteams/ms3/entity/SystemUser.java`
- `src/main/java/org/cswteams/ms3/entity/constraint/AdditionalConstraint.java`
- `src/main/java/org/cswteams/ms3/entity/constraint/ConstraintAssegnazioneTurnoTurno.java`
- `src/main/java/org/cswteams/ms3/entity/constraint/ConstraintHoliday.java`
- `src/main/java/org/cswteams/ms3/entity/constraint/ConstraintMaxOrePeriodo.java`
- `src/main/java/org/cswteams/ms3/entity/constraint/ConstraintMaxPeriodoConsecutivo.java`
- `src/main/java/org/cswteams/ms3/entity/constraint/ConstraintNumeroDiRuoloTurno.java`
- `src/main/java/org/cswteams/ms3/entity/constraint/ConstraintTurniContigui.java`
- `src/main/java/org/cswteams/ms3/entity/constraint/ConstraintUbiquita.java`
- `src/main/java/org/cswteams/ms3/entity/constraint/ContextConstraint.java`
- `src/main/java/org/cswteams/ms3/entity/enums/FeedbackCategory.java`
- `src/main/java/org/cswteams/ms3/entity/scocciature/ScocciaturaAssegnazioneUtente.java`
- `src/main/java/org/cswteams/ms3/entity/scocciature/ScocciaturaDesiderata.java`
- `src/main/java/org/cswteams/ms3/entity/scocciature/ScocciaturaVacanza.java`
- `src/main/java/org/cswteams/ms3/filters/JwtRequestFilters.java`
- `src/main/java/org/cswteams/ms3/filters/RequestCorrelationFilter.java`
- `src/main/java/org/cswteams/ms3/rest/ComparisonRestEndpoint.java`
- `src/main/java/org/cswteams/ms3/rest/LoginRestEndpoint.java`
- `src/main/java/org/cswteams/ms3/rest/LogoutRestEndpoint.java`
- `src/main/java/org/cswteams/ms3/rest/NotificationRestEndpoint.java`
- `src/main/java/org/cswteams/ms3/rest/ScheduleFeedbackRestEndpoint.java`
- `src/main/java/org/cswteams/ms3/rest/ScheduleRestEndpoint.java`
- `src/main/java/org/cswteams/ms3/rest/TwoFactorRestEndpoint.java`
- `src/main/java/org/cswteams/ms3/rest/UsersRestEndpoint.java`
- `src/main/java/org/cswteams/ms3/security/BlacklistService.java`
- `src/main/java/org/cswteams/ms3/security/SecurityConfigurer.java`
- `src/main/java/org/cswteams/ms3/security/TwoFactorAuthenticationService.java`
- `src/main/java/org/cswteams/ms3/security/TwoFactorCodeService.java`
- `src/main/java/org/cswteams/ms3/security/TwoFactorProperties.java`
- `src/main/java/org/cswteams/ms3/security/TwoFactorResult.java`
- `src/main/java/org/cswteams/ms3/security/TwoFactorVerificationOutcome.java`
- `src/main/java/org/cswteams/ms3/utils/JwtUtil.java`
- `src/main/java/org/cswteams/ms3/utils/TurnstileService.java`
- `src/main/resources/Dockerfile.backend`
- `src/main/resources/Dockerfile.backend.debug`
- `src/main/resources/ai/system_prompt_template.txt`
- `src/main/resources/application-container.properties`
- `src/main/resources/application.properties`
- `src/main/resources/db/assign_privileges.sql`
- `src/main/resources/db/create_blacklisted_tokens.sql`
- `src/main/resources/db/create_schemas.sql`
- `src/main/resources/db/create_system_user_tables.sql`
- `src/main/resources/db/init-scripts/init-users.sh`
- `src/main/resources/db/migration/V1__add_2fa_state_columns.sql`
- `src/main/resources/db/migration/V2__migrate_feedback_category_to_postgres_enum.sql`
- `src/main/resources/db/migration/V3__feedback_category_enum_to_varchar_check.sql`
- `src/main/resources/db/migration/V4__feedback_category_varchar_to_pg_enum.sql`
- `src/main/resources/db/tenant/sequences/create_sequence.sql`
- `src/main/resources/db/tenant/tables/create_feedback_tables.sql`
- `src/main/resources/db/tenant/tables/create_tenant_user_tables.sql`
- `src/main/resources/doctors_seed_fac_simile.json`
- `src/test/java/org/cswteams/ms3/AbstractMultiTenantIntegrationTest.java`
- `src/test/java/org/cswteams/ms3/ai/broker/AgentBrokerImplTest.java`
- `src/test/java/org/cswteams/ms3/ai/broker/AiPromptBuilderTest.java`
- `src/test/java/org/cswteams/ms3/ai/broker/AiPromptTemplateTest.java`
- `src/test/java/org/cswteams/ms3/ai/broker/mapper/AiScheduleResponseMapperTest.java`
- `src/test/java/org/cswteams/ms3/ai/decision/DecisionAlgorithmServiceTest.java`
- `src/test/java/org/cswteams/ms3/ai/metrics/MetricUtilitiesTest.java`
- `src/test/java/org/cswteams/ms3/ai/orchestration/AiActiveConstraintResolverTest.java`
- `src/test/java/org/cswteams/ms3/ai/orchestration/AiHardCoveragePromptBlockBuilderTest.java`
- `src/test/java/org/cswteams/ms3/ai/orchestration/AiReschedulingOrchestrationServiceTest.java`
- `src/test/java/org/cswteams/ms3/ai/orchestration/AiRoleValidationScratchpadPromptBlockBuilderTest.java`
- `src/test/java/org/cswteams/ms3/ai/orchestration/AiScheduleGenerationOrchestrationServiceRetryValidationTest.java`
- `src/test/java/org/cswteams/ms3/ai/orchestration/AiScheduleGenerationOrchestrationServiceSelectionPersistenceTest.java`
- `src/test/java/org/cswteams/ms3/ai/orchestration/AiScheduleGenerationOrchestrationServiceTest.java`
- `src/test/java/org/cswteams/ms3/ai/priority/PriorityScaleConfigTest.java`
- `src/test/java/org/cswteams/ms3/ai/protocol/AiScheduleJsonParserTest.java`
- `src/test/java/org/cswteams/ms3/ai/protocol/AiScheduleSemanticValidatorTest.java`
- `src/test/java/org/cswteams/ms3/ai/protocol/converter/AiScheduleConverterServiceTest.java`
- `src/test/java/org/cswteams/ms3/audit/selection/AuditSelectionAspectTest.java`
- `src/test/java/org/cswteams/ms3/audit/validation/MetricComputationValidatorTest.java`
- `src/test/java/org/cswteams/ms3/control/cambiaPassword/ControllerPasswordTest.java`
- `src/test/java/org/cswteams/ms3/control/controllerscheduler/ScheduleTests.java`
- `src/test/java/org/cswteams/ms3/control/logout/TestJwtBlacklistService.java`
- `src/test/java/org/cswteams/ms3/control/logout/TestLogoutController.java`
- `src/test/java/org/cswteams/ms3/control/logout/TestLogoutIT.java`
- `src/test/java/org/cswteams/ms3/control/passwordChange/PasswordChangeIT.java`
- `src/test/java/org/cswteams/ms3/control/passwordChange/PasswordChangeTest.java`
- `src/test/java/org/cswteams/ms3/control/scheduleFeedback/ScheduleFeedbackControllerTest.java`
- `src/test/java/org/cswteams/ms3/control/scheduler/SchedulerControllerTest.java`
- `src/test/java/org/cswteams/ms3/control/scheduler/constraint_tests/ControllerSchedulerTest.java`
- `src/test/java/org/cswteams/ms3/control/scocciatura/ControllerScocciaturaTest.java`
- `src/test/java/org/cswteams/ms3/control/toon/ToonBuilderTest.java`
- `src/test/java/org/cswteams/ms3/control/toon/ToonValidatorTest.java`
- `src/test/java/org/cswteams/ms3/entity/DoctorUffaPriorityTest.java`
- `src/test/java/org/cswteams/ms3/entity/constraint/ConstraintMaxOrePeriodoTest.java`
- `src/test/java/org/cswteams/ms3/filters/JwtRequestFiltersTest.java`
- `src/test/java/org/cswteams/ms3/filters/RequestCorrelationFilterTest.java`
- `src/test/java/org/cswteams/ms3/rest/ComparisonRestEndpointIT.java`
- `src/test/java/org/cswteams/ms3/rest/LogoutRestEndpointIT.java`
- `src/test/java/org/cswteams/ms3/rest/LogoutRestEndpointTest.java`
- `src/test/java/org/cswteams/ms3/rest/ScheduleFeedbackRestEndpointTest.java`
- `src/test/java/org/cswteams/ms3/rest/ScheduleRestEndpointSelectionIT.java`
- `src/test/java/org/cswteams/ms3/service/tokenRemoval/ExpiredTokensRemovalServiceIT.java`
- `src/test/java/org/cswteams/ms3/service/tokenRemoval/ExpiredTokensRemovalServiceTest.java`
- `src/test/resources/ai/simulated-schedule.json`
- `src/test/resources/application-test.properties`

### Excluded entries (`reverted_only=yes` / `in_scope=no`)
- `GPT_PROJECT_INSTRUCTIONS.md`
- `docs/AI_powered_rescheduling/roadmap_sprint_4.md`
- `docs/GPT/General/GPT_PROJECT_INSTRUCTIONS.md`
- `frontend/public/index.html`
- `src/main/java/org/cswteams/ms3/control/scheduler/ConstraintCheckResult.java`
- `src/main/java/org/cswteams/ms3/control/scheduler/ConstraintEnforcementMode.java`
- `src/main/java/org/cswteams/ms3/control/scheduler/ConstraintViolationSeverity.java`
- `src/main/java/org/cswteams/ms3/control/utils/RispostaViolazioneVincoli.java`
- `src/main/java/org/cswteams/ms3/dto/login/LoginFailureDTO.java`
- `src/main/java/org/cswteams/ms3/rest/ConcreteShiftRestEndpoint.java`
- `src/main/java/org/cswteams/ms3/security/LoginAttemptService.java`
- `src/main/java/org/cswteams/ms3/security/TwoFactorConfiguration.java`
- `src/test/java/org/cswteams/ms3/control/scheduler/ScheduleBuilderConstraintSeverityTest.java`
<!-- LOCKED_CHECKLIST_END -->
