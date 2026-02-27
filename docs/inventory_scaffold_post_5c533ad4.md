# Post-`5c533ad4` Machine-Assisted Inventory Scaffold

Source filters applied: `git diff --name-only 5c533ad4..HEAD` intersected with `commenting_scope_post_5c533ad4.csv`, excluding rows where `reverted_only=yes`.

## AI scheduling

| File | Package | Object type |
|---|---|---|
| `docs/AI_powered_rescheduling/ai_prompt_pipeline_extension_points.md` | `docs.AI_powered_rescheduling` | `doc` |
| `docs/AI_powered_rescheduling/dual_layer_coverage_monitoring.md` | `docs.AI_powered_rescheduling` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/README.md` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/ai_agent_comparison.md` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/ai_rescheduling_orchestration_flow.drawio` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/checkbox_roadmap_sprint_4.md` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/retrospective_inputs.md` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/roadmap_sprint_4.md` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/sprint_summary.md` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/stakeholder_slide_deck_outline.md` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/story_1.md` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/story_2.md` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/story_3.md` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/story_4.md` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/story_5.md` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/AI_powered_rescheduling/sprint_4/technical_doc_draft.md` | `docs.AI_powered_rescheduling.sprint_4` | `doc` |
| `docs/data_generation/doctor_attributes_for_scheduling_as_is.md` | `docs.data_generation` | `doc` |
| `docs/scheduling/AI-Powered_scheduling_analysis.md` | `docs.scheduling` | `doc` |
| `docs/scheduling/AI_agents_architectural_analysis.md` | `docs.scheduling` | `doc` |
| `docs/scheduling/AI_agents_in-depth_analysis.md` | `docs.scheduling` | `doc` |
| `docs/scheduling/AI_agents_preliminar_analysis.md` | `docs.scheduling` | `doc` |
| `docs/scheduling/scheduling_as-is_analysis.md` | `docs.scheduling` | `doc` |
| `docs/scheduling_flow/ai_generation_selection_flow.md` | `docs.scheduling_flow` | `doc` |
| `src/main/java/org/cswteams/ms3/ai/broker/AgentBroker.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/AgentBrokerImpl.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/AgentProvider.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/AgentProviderAdapter.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/AiBrokerConfiguration.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/AiBrokerProperties.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/AiBrokerRequest.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/AiPromptTemplate.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/AiTokenBudgetGuardResult.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/AiTokenEstimator.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/AiTokenUsageTracker.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/GemmaAgentAdapter.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/Llama70bAgentAdapter.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/domain/AiAssignment.java` | `org.cswteams.ms3.ai.broker.domain` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/domain/AiMetadata.java` | `org.cswteams.ms3.ai.broker.domain` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/domain/AiMetrics.java` | `org.cswteams.ms3.ai.broker.domain` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/domain/AiScheduleResponse.java` | `org.cswteams.ms3.ai.broker.domain` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/domain/AiScheduleVariantsResponse.java` | `org.cswteams.ms3.ai.broker.domain` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/domain/AiStdDev.java` | `org.cswteams.ms3.ai.broker.domain` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/domain/AiUffaBalance.java` | `org.cswteams.ms3.ai.broker.domain` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/domain/AiUffaDelta.java` | `org.cswteams.ms3.ai.broker.domain` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/domain/AiUncoveredShift.java` | `org.cswteams.ms3.ai.broker.domain` | `service` |
| `src/main/java/org/cswteams/ms3/ai/broker/mapper/AiScheduleResponseMapper.java` | `org.cswteams.ms3.ai.broker.mapper` | `service` |
| `src/main/java/org/cswteams/ms3/ai/comparison/domain/AiScheduleComparisonCandidate.java` | `org.cswteams.ms3.ai.comparison.domain` | `service` |
| `src/main/java/org/cswteams/ms3/ai/comparison/domain/AiScheduleDecisionOutcome.java` | `org.cswteams.ms3.ai.comparison.domain` | `service` |
| `src/main/java/org/cswteams/ms3/ai/comparison/domain/DecisionMetricValues.java` | `org.cswteams.ms3.ai.comparison.domain` | `service` |
| `src/main/java/org/cswteams/ms3/ai/comparison/domain/ScheduleCandidateType.java` | `org.cswteams.ms3.ai.comparison.domain` | `service` |
| `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleCandidateMetadataDto.java` | `org.cswteams.ms3.ai.comparison.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleComparisonCandidateDto.java` | `org.cswteams.ms3.ai.comparison.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleComparisonResponseDto.java` | `org.cswteams.ms3.ai.comparison.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleDecisionMetricValuesDto.java` | `org.cswteams.ms3.ai.comparison.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleDecisionMetricsDto.java` | `org.cswteams.ms3.ai.comparison.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleDecisionOutcomeDto.java` | `org.cswteams.ms3.ai.comparison.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/comparison/dto/AiScheduleSelectionRequestDto.java` | `org.cswteams.ms3.ai.comparison.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/comparison/mapper/AiScheduleComparisonMapper.java` | `org.cswteams.ms3.ai.comparison.mapper` | `service` |
| `src/main/java/org/cswteams/ms3/ai/decision/AiScheduleCandidateMetrics.java` | `org.cswteams.ms3.ai.decision` | `service` |
| `src/main/java/org/cswteams/ms3/ai/decision/DecisionAlgorithmService.java` | `org.cswteams.ms3.ai.decision` | `service` |
| `src/main/java/org/cswteams/ms3/ai/decision/DecisionAlgorithmServiceImpl.java` | `org.cswteams.ms3.ai.decision` | `service` |
| `src/main/java/org/cswteams/ms3/ai/metrics/MetricAggregationUtils.java` | `org.cswteams.ms3.ai.metrics` | `service` |
| `src/main/java/org/cswteams/ms3/ai/metrics/MetricNormalizationUtils.java` | `org.cswteams.ms3.ai.metrics` | `service` |
| `src/main/java/org/cswteams/ms3/ai/metrics/UffaDeltaStats.java` | `org.cswteams.ms3.ai.metrics` | `service` |
| `src/main/java/org/cswteams/ms3/ai/orchestration/AiActiveConstraintResolver.java` | `org.cswteams.ms3.ai.orchestration` | `service` |
| `src/main/java/org/cswteams/ms3/ai/orchestration/AiHardCoveragePromptBlockBuilder.java` | `org.cswteams.ms3.ai.orchestration` | `service` |
| `src/main/java/org/cswteams/ms3/ai/orchestration/AiReschedulingOrchestrationService.java` | `org.cswteams.ms3.ai.orchestration` | `service` |
| `src/main/java/org/cswteams/ms3/ai/orchestration/AiReschedulingToonRequest.java` | `org.cswteams.ms3.ai.orchestration` | `service` |
| `src/main/java/org/cswteams/ms3/ai/orchestration/AiRoleValidationScratchpadPromptBlockBuilder.java` | `org.cswteams.ms3.ai.orchestration` | `service` |
| `src/main/java/org/cswteams/ms3/ai/orchestration/AiScheduleGenerationOrchestrationService.java` | `org.cswteams.ms3.ai.orchestration` | `service` |
| `src/main/java/org/cswteams/ms3/ai/priority/PriorityDimension.java` | `org.cswteams.ms3.ai.priority` | `service` |
| `src/main/java/org/cswteams/ms3/ai/priority/PriorityScaleConfig.java` | `org.cswteams.ms3.ai.priority` | `service` |
| `src/main/java/org/cswteams/ms3/ai/priority/PriorityScaleProperties.java` | `org.cswteams.ms3.ai.priority` | `service` |
| `src/main/java/org/cswteams/ms3/ai/priority/PriorityScaleValidationException.java` | `org.cswteams.ms3.ai.priority` | `service` |
| `src/main/java/org/cswteams/ms3/ai/protocol/AiScheduleJsonParser.java` | `org.cswteams.ms3.ai.protocol` | `service` |
| `src/main/java/org/cswteams/ms3/ai/protocol/AiScheduleSemanticValidator.java` | `org.cswteams.ms3.ai.protocol` | `service` |
| `src/main/java/org/cswteams/ms3/ai/protocol/AiUffaDeltaDeserializer.java` | `org.cswteams.ms3.ai.protocol` | `service` |
| `src/main/java/org/cswteams/ms3/ai/protocol/ValidationError.java` | `org.cswteams.ms3.ai.protocol` | `service` |
| `src/main/java/org/cswteams/ms3/ai/protocol/converter/AiScheduleConverterService.java` | `org.cswteams.ms3.ai.protocol.converter` | `service` |
| `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiAssignmentDto.java` | `org.cswteams.ms3.ai.protocol.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiMetadataDto.java` | `org.cswteams.ms3.ai.protocol.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiMetricsDto.java` | `org.cswteams.ms3.ai.protocol.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiRoleCoveredDeserializer.java` | `org.cswteams.ms3.ai.protocol.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiRoleValidationScratchpadItemDto.java` | `org.cswteams.ms3.ai.protocol.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiScheduleResponseDto.java` | `org.cswteams.ms3.ai.protocol.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiScheduleVariantsResponseDto.java` | `org.cswteams.ms3.ai.protocol.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiStdDevDto.java` | `org.cswteams.ms3.ai.protocol.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiUffaBalanceDto.java` | `org.cswteams.ms3.ai.protocol.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiUffaDeltaDto.java` | `org.cswteams.ms3.ai.protocol.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/protocol/dto/AiUncoveredShiftDto.java` | `org.cswteams.ms3.ai.protocol.dto` | `dto` |
| `src/main/java/org/cswteams/ms3/ai/protocol/exceptions/AiProtocolException.java` | `org.cswteams.ms3.ai.protocol.exceptions` | `service` |
| `src/main/java/org/cswteams/ms3/ai/protocol/utils/AiStatus.java` | `org.cswteams.ms3.ai.protocol.utils` | `service` |
| `src/main/java/org/cswteams/ms3/ai/protocol/utils/AiUffaQueue.java` | `org.cswteams.ms3.ai.protocol.utils` | `service` |
| `src/main/java/org/cswteams/ms3/audit/selection/SelectionAuditEvent.java` | `org.cswteams.ms3.audit.selection` | `service` |
| `src/main/java/org/cswteams/ms3/control/scheduler/ISchedulerController.java` | `org.cswteams.ms3.control.scheduler` | `controller` |
| `src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java` | `org.cswteams.ms3.control.scheduler` | `service` |
| `src/main/java/org/cswteams/ms3/control/scheduler/SchedulerController.java` | `org.cswteams.ms3.control.scheduler` | `controller` |
| `src/main/java/org/cswteams/ms3/dao/SelectionAuditRecordRepository.java` | `org.cswteams.ms3.dao` | `service` |
| `src/main/java/org/cswteams/ms3/entity/SelectionAuditRecord.java` | `org.cswteams.ms3.entity` | `entity` |
| `src/main/java/org/cswteams/ms3/rest/ComparisonRestEndpoint.java` | `org.cswteams.ms3.rest` | `controller` |
| `src/main/resources/ai/system_prompt_template.txt` | `src.main.resources` | `doc` |
| `src/test/java/org/cswteams/ms3/ai/broker/AgentBrokerImplTest.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/test/java/org/cswteams/ms3/ai/broker/AiPromptBuilderTest.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/test/java/org/cswteams/ms3/ai/broker/AiPromptTemplateTest.java` | `org.cswteams.ms3.ai.broker` | `service` |
| `src/test/java/org/cswteams/ms3/ai/broker/mapper/AiScheduleResponseMapperTest.java` | `org.cswteams.ms3.ai.broker.mapper` | `service` |
| `src/test/java/org/cswteams/ms3/ai/decision/DecisionAlgorithmServiceTest.java` | `org.cswteams.ms3.ai.decision` | `service` |
| `src/test/java/org/cswteams/ms3/ai/metrics/MetricUtilitiesTest.java` | `org.cswteams.ms3.ai.metrics` | `service` |
| `src/test/java/org/cswteams/ms3/ai/orchestration/AiActiveConstraintResolverTest.java` | `org.cswteams.ms3.ai.orchestration` | `service` |
| `src/test/java/org/cswteams/ms3/ai/orchestration/AiHardCoveragePromptBlockBuilderTest.java` | `org.cswteams.ms3.ai.orchestration` | `service` |
| `src/test/java/org/cswteams/ms3/ai/orchestration/AiReschedulingOrchestrationServiceTest.java` | `org.cswteams.ms3.ai.orchestration` | `service` |
| `src/test/java/org/cswteams/ms3/ai/orchestration/AiRoleValidationScratchpadPromptBlockBuilderTest.java` | `org.cswteams.ms3.ai.orchestration` | `service` |
| `src/test/java/org/cswteams/ms3/ai/orchestration/AiScheduleGenerationOrchestrationServiceRetryValidationTest.java` | `org.cswteams.ms3.ai.orchestration` | `service` |
| `src/test/java/org/cswteams/ms3/ai/orchestration/AiScheduleGenerationOrchestrationServiceSelectionPersistenceTest.java` | `org.cswteams.ms3.ai.orchestration` | `service` |
| `src/test/java/org/cswteams/ms3/ai/orchestration/AiScheduleGenerationOrchestrationServiceTest.java` | `org.cswteams.ms3.ai.orchestration` | `service` |
| `src/test/java/org/cswteams/ms3/ai/priority/PriorityScaleConfigTest.java` | `org.cswteams.ms3.ai.priority` | `service` |
| `src/test/java/org/cswteams/ms3/ai/protocol/AiScheduleJsonParserTest.java` | `org.cswteams.ms3.ai.protocol` | `service` |
| `src/test/java/org/cswteams/ms3/ai/protocol/AiScheduleSemanticValidatorTest.java` | `org.cswteams.ms3.ai.protocol` | `service` |
| `src/test/java/org/cswteams/ms3/ai/protocol/converter/AiScheduleConverterServiceTest.java` | `org.cswteams.ms3.ai.protocol.converter` | `service` |
| `src/test/java/org/cswteams/ms3/control/scheduler/SchedulerControllerTest.java` | `org.cswteams.ms3.control.scheduler` | `controller` |
| `src/test/java/org/cswteams/ms3/control/scheduler/constraint_tests/ControllerSchedulerTest.java` | `org.cswteams.ms3.control.scheduler.constraint_tests` | `controller` |
| `src/test/java/org/cswteams/ms3/rest/ComparisonRestEndpointIT.java` | `org.cswteams.ms3.rest` | `controller` |
| `src/test/resources/ai/simulated-schedule.json` | `(root)` | `doc` |

## Feedback orchestration

| File | Package | Object type |
|---|---|---|
| `frontend/src/API/ScheduleFeedbackAPI.js` | `API` | `api` |
| `frontend/src/components/common/GenerationStatusFeedback.js` | `components.common` | `ui` |
| `frontend/src/views/pianificatore/FeedbackManagementView.js` | `views.pianificatore` | `ui` |
| `src/main/java/org/cswteams/ms3/control/scheduleFeedback/IScheduleFeedbackController.java` | `org.cswteams.ms3.control.scheduleFeedback` | `controller` |
| `src/main/java/org/cswteams/ms3/control/scheduleFeedback/ScheduleFeedbackController.java` | `org.cswteams.ms3.control.scheduleFeedback` | `controller` |
| `src/main/java/org/cswteams/ms3/control/toon/ToonFeedback.java` | `org.cswteams.ms3.control.toon` | `service` |
| `src/main/java/org/cswteams/ms3/dao/ScheduleFeedbackDAO.java` | `org.cswteams.ms3.dao` | `service` |
| `src/main/java/org/cswteams/ms3/dto/scheduleFeedback/ScheduleFeedbackDTO.java` | `org.cswteams.ms3.dto.scheduleFeedback` | `dto` |
| `src/main/java/org/cswteams/ms3/entity/ScheduleFeedback.java` | `org.cswteams.ms3.entity` | `entity` |
| `src/main/java/org/cswteams/ms3/entity/enums/FeedbackCategory.java` | `org.cswteams.ms3.entity.enums` | `entity` |
| `src/main/java/org/cswteams/ms3/rest/ScheduleFeedbackRestEndpoint.java` | `org.cswteams.ms3.rest` | `controller` |
| `src/main/resources/db/migration/V2__migrate_feedback_category_to_postgres_enum.sql` | `src.main.resources` | `doc` |
| `src/main/resources/db/migration/V3__feedback_category_enum_to_varchar_check.sql` | `src.main.resources` | `doc` |
| `src/main/resources/db/migration/V4__feedback_category_varchar_to_pg_enum.sql` | `src.main.resources` | `doc` |
| `src/main/resources/db/tenant/tables/create_feedback_tables.sql` | `src.main.resources` | `doc` |
| `src/test/java/org/cswteams/ms3/control/scheduleFeedback/ScheduleFeedbackControllerTest.java` | `org.cswteams.ms3.control.scheduleFeedback` | `controller` |
| `src/test/java/org/cswteams/ms3/rest/ScheduleFeedbackRestEndpointTest.java` | `org.cswteams.ms3.rest` | `controller` |

## 2FA

| File | Package | Object type |
|---|---|---|
| `docs/security/2fa_totp_codex_implementation_prompts.md` | `docs.security` | `doc` |
| `docs/security/2fa_totp_design.md` | `docs.security` | `doc` |
| `frontend/src/API/TwoFactorAPI.js` | `API` | `api` |
| `frontend/src/views/utente/TwoFactorEnrollmentView.js` | `views.utente` | `ui` |
| `frontend/src/views/utente/TwoFactorEnrollmentView.test.js` | `views.utente` | `ui` |
| `src/main/java/org/cswteams/ms3/rest/TwoFactorRestEndpoint.java` | `org.cswteams.ms3.rest` | `controller` |
| `src/main/java/org/cswteams/ms3/security/TwoFactorAuthenticationService.java` | `org.cswteams.ms3.security` | `service` |
| `src/main/java/org/cswteams/ms3/security/TwoFactorCodeService.java` | `org.cswteams.ms3.security` | `service` |
| `src/main/java/org/cswteams/ms3/security/TwoFactorProperties.java` | `org.cswteams.ms3.security` | `service` |
| `src/main/java/org/cswteams/ms3/security/TwoFactorResult.java` | `org.cswteams.ms3.security` | `service` |
| `src/main/java/org/cswteams/ms3/security/TwoFactorVerificationOutcome.java` | `org.cswteams.ms3.security` | `service` |
| `src/main/resources/db/migration/V1__add_2fa_state_columns.sql` | `src.main.resources` | `doc` |

## Captcha/logout

| File | Package | Object type |
|---|---|---|
| `frontend/src/API/LogoutAPI.js` | `API` | `api` |
| `frontend/src/components/common/TurnstileWidget.js` | `components.common` | `ui` |
| `src/main/java/org/cswteams/ms3/control/logout/ExpiredTokensRemovalService.java` | `org.cswteams.ms3.control.logout` | `service` |
| `src/main/java/org/cswteams/ms3/control/logout/ILogoutController.java` | `org.cswteams.ms3.control.logout` | `controller` |
| `src/main/java/org/cswteams/ms3/control/logout/JwtBlacklistService.java` | `org.cswteams.ms3.control.logout` | `service` |
| `src/main/java/org/cswteams/ms3/control/logout/LogoutController.java` | `org.cswteams.ms3.control.logout` | `controller` |
| `src/main/java/org/cswteams/ms3/dao/BlacklistedTokenDAO.java` | `org.cswteams.ms3.dao` | `service` |
| `src/main/java/org/cswteams/ms3/entity/BlacklistedToken.java` | `org.cswteams.ms3.entity` | `entity` |
| `src/main/java/org/cswteams/ms3/rest/LogoutRestEndpoint.java` | `org.cswteams.ms3.rest` | `controller` |
| `src/main/java/org/cswteams/ms3/utils/TurnstileService.java` | `org.cswteams.ms3.utils` | `service` |
| `src/test/java/org/cswteams/ms3/control/logout/TestJwtBlacklistService.java` | `org.cswteams.ms3.control.logout` | `service` |
| `src/test/java/org/cswteams/ms3/control/logout/TestLogoutController.java` | `org.cswteams.ms3.control.logout` | `controller` |
| `src/test/java/org/cswteams/ms3/control/logout/TestLogoutIT.java` | `org.cswteams.ms3.control.logout` | `service` |
| `src/test/java/org/cswteams/ms3/rest/LogoutRestEndpointIT.java` | `org.cswteams.ms3.rest` | `controller` |
| `src/test/java/org/cswteams/ms3/rest/LogoutRestEndpointTest.java` | `org.cswteams.ms3.rest` | `controller` |

## DB/bootstrap

| File | Package | Object type |
|---|---|---|
| `docker-compose-debug.yml` | `(root)` | `doc` |
| `docker-compose.yml` | `(root)` | `doc` |
| `docs/testing/db-bootstrap.md` | `docs.testing` | `doc` |
| `doctors_seed.json` | `(root)` | `doc` |
| `doctors_seed.sql` | `(root)` | `doc` |
| `doctors_seed.toon` | `(root)` | `doc` |
| `src/main/java/org/cswteams/ms3/config/ApplicationStartup.java` | `org.cswteams.ms3.config` | `service` |
| `src/main/java/org/cswteams/ms3/config/multitenancy/SchemaSwitchingConnectionProviderPostgreSQL.java` | `org.cswteams.ms3.config.multitenancy` | `service` |
| `src/main/java/org/cswteams/ms3/config/multitenancy/SchemasInitializer.java` | `org.cswteams.ms3.config.multitenancy` | `service` |
| `src/main/resources/db/create_schemas.sql` | `src.main.resources` | `doc` |
| `src/main/resources/doctors_seed_fac_simile.json` | `src.main.resources` | `doc` |

## Frontend UX

| File | Package | Object type |
|---|---|---|
| `frontend/.env` | `(root)` | `ui` |
| `frontend/package.json` | `(root)` | `ui` |
| `frontend/src/API/AssegnazioneTurnoAPI.js` | `API` | `api` |
| `frontend/src/API/LoginAPI.js` | `API` | `api` |
| `frontend/src/API/NotificationAPI.js` | `API` | `api` |
| `frontend/src/API/ScheduleAPI.js` | `API` | `api` |
| `frontend/src/components/common/AiScheduleComparisonModal.js` | `components.common` | `ui` |
| `frontend/src/components/common/AiScheduleSelectionConfirmationModal.js` | `components.common` | `ui` |
| `frontend/src/components/common/BottomViewAggiungiSchedulazione.js` | `components.common` | `ui` |
| `frontend/src/components/common/CustomAppointmentComponents.js` | `components.common` | `ui` |
| `frontend/src/components/common/GenerationLoadingModal.js` | `components.common` | `ui` |
| `frontend/src/components/layout/MainFooter.js` | `components.layout` | `ui` |
| `frontend/src/components/layout/MainNavbar/NavbarNav/UserActions.js` | `components.layout.MainNavbar.NavbarNav` | `ui` |
| `frontend/src/data/sidebar-nav-items.js` | `data` | `ui` |
| `frontend/src/locales/en.json` | `locales` | `ui` |
| `frontend/src/locales/it.json` | `locales` | `ui` |
| `frontend/src/routes.js` | `routes.js` | `ui` |
| `frontend/src/views/pianificatore/ScheduleGeneratorView.js` | `views.pianificatore` | `ui` |
| `frontend/src/views/utente/LoginView.js` | `views.utente` | `ui` |
| `frontend/src/views/utente/LoginView.test.js` | `views.utente` | `ui` |

