# AI Prompt Pipeline — Extension Points (Implementation Note)

## 1) Orchestration entry points that assemble prompt inputs

- `AiScheduleGenerationOrchestrationService.generateScheduleComparison(...)` is the top-level orchestration entry for generation and triggers AI candidate requests.
- `AiScheduleGenerationOrchestrationService.buildToonPayload(...)` assembles the TOON payload from runtime schedule context, then appends runtime blocks:
  - `hard_coverage_requirements[...]`
  - `role_validation_scratchpad[...]`
- `AiScheduleGenerationOrchestrationService.requestAiCandidates(...)` creates `AiBrokerRequest(toonPayload, attemptInstructions, correlationId)` and sends it to `AgentBroker`.

## 2) Coverage block builder(s) and JSON payload producer(s)

- Coverage block source:
  - Class: `AiHardCoveragePromptBlockBuilder`
  - Method: `buildHardCoverageRequirementsBlock(List<ConcreteShift>)`
  - It renders `hard_coverage_requirements[n]{...}` rows from shift seniority requirements.
- Related runtime block source (same pipeline stage):
  - Class: `AiRoleValidationScratchpadPromptBlockBuilder`
  - Method: `buildRoleValidationScratchpadBlock(List<ConcreteShift>, List<Doctor>)`
- Provider JSON payload production:
  - `Llama70bAgentAdapter.buildPayloadJson(...)` serializes model request JSON (`messages`) containing the user and system prompt strings.
  - `GemmaAgentAdapter.buildPayloadJson(...)` serializes model request JSON (`contents`) containing the user and system prompt strings.

## 3) Where `system_prompt_template.txt` is loaded and merged

- `AiPromptTemplate` owns system-template loading:
  - Constant resource path: `TEMPLATE_RESOURCE = "/ai/system_prompt_template.txt"`
  - Loader: `loadTemplate()` via `AiPromptTemplate.class.getResourceAsStream(...)`
- Merge rules:
  - `systemPrompt()` returns template-only system prompt.
  - `buildUserContent(instructions, toonPayload)` composes runtime user content (`Instructions`, fixed policy lines, and `TOON_INPUT`).
- Adapter call sites:
  - `Llama70bAgentAdapter.execute(...)` fetches `systemPrompt()` + `buildUserContent(...)`.
  - `GemmaAgentAdapter.execute(...)` fetches `systemPrompt()` + `buildUserContent(...)`.

## 4) Ownership split (to avoid scope drift)

- **System instructions owner:** `AiPromptTemplate` + `src/main/resources/ai/system_prompt_template.txt`.
- **User payload owner (runtime data):** `AiScheduleGenerationOrchestrationService` (`buildToonPayload`, per-attempt `attemptInstructions`) plus block builders (`AiHardCoveragePromptBlockBuilder`, `AiRoleValidationScratchpadPromptBlockBuilder`).
- **Transport JSON owner:** provider adapters (`Llama70bAgentAdapter`, `GemmaAgentAdapter`) that wrap the two prompt strings into provider-specific JSON schemas.
