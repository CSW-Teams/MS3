# Sprint 4 Roadmap — AI-Powered Rescheduling (2-Week Sprint)

## Sprint Overview & Assumptions
- **Team capacity**: 5 people × ~14 hours = **70 total hours** for the sprint.
- **Sprint cadence**: 2 weeks, with a **mid-sprint alignment meeting** on Monday of week 2 and **sprint review + retro** the following Monday.
- **Scope**: Analysis + planning + implementation for AI-powered rescheduling with backend + frontend + AI-agent communication + metrics + documentation + stakeholder materials.
- **Code change policy for this sprint roadmap**: Includes implementation tasks required to deliver all sprint stories.
- **Key existing flow anchors**: Schedule generation/regeneration uses `/api/schedule/generation` and `/api/schedule/regeneration` in the backend and frontend schedule generator view. The current system generates schedules from the planner UI with a loading overlay and a list of schedules, but has **no AI integration or schedule comparison UI** yet.
- **AI models selected**: Gemini **Gemma** and Grok **Llama-70B**.
- **Planner-only scope**: AI rescheduling and final schedule selection are **planner-only** during the rescheduling phase, then the chosen schedule replaces the algorithm-generated schedule in full.

## Story 1 — Current Scheduling Flow Analysis & Baseline Mapping
**Goal**: Document the current scheduling generation/regeneration flow, the scheduling data model, and constraint pipeline to ground AI-powered rescheduling requirements.

**Functional Scope**
- Backend schedule generation/regeneration endpoints and controller orchestration.
- Schedule-building constraints and priority queues (UFFA/scocciatura).
- Frontend planner UI for schedule generation and schedule list.

**Estimated Effort**: **12 hours** (6 microtasks; 3 completed × 2h, 3 dev microtasks × 2h)

**Dependencies**: None (starting point).

**Expected Deliverables**
- Baseline flow map (sequence of REST calls and data dependencies).
- List of current UI surfaces involved in scheduling.

**Sprint Timing**: **Early sprint** (needed for all downstream stories).

**Microtasks**
1. **Map backend schedule generation/regeneration flow** (**2h, Completed by Cappellini**)
   - Description: Identify entrypoints, controller orchestration, and data sources used to build schedules.
   - Preconditions: None.
   - Parallel affinities: None.
   - Output artifact: Backend flow diagram / notes in roadmap appendix.
2. **Catalog constraint and priority pipeline** (**2h, Completed by Capone**)
   - Description: Summarize how constraints and UFFA/scocciatura priorities influence schedule generation.
   - Preconditions: Microtask 1.
   - Parallel affinities: None.
   - Output artifact: Constraint/priority summary and risks list.
3. **Map current planner UI scheduling surfaces** (**2h, Completed by Cappellini**)
   - Description: Inventory schedule generation UI, loading states, schedule list presentation, and regeneration actions.
   - Preconditions: None.
   - Parallel affinities: None.
   - Output artifact: UI touchpoints list for planner flow.
4. **Add backend flow trace logging for generation/regeneration** (**2h, Completed by Capone**)
   - Description: Implement structured logging for generation/regeneration entrypoints to support AI orchestration debugging.
   - Preconditions: Microtasks 1–2.
   - Parallel affinities: None.
   - Output artifact: Logging configuration + sample traces.
5. **Add regression tests for current schedule generation endpoints** (**2h, Completed by Cantarini**)
   - Description: Create baseline tests that assert the existing generation/regeneration contract to prevent AI changes from breaking current behavior.
   - Preconditions: Microtasks 1–3.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Endpoint regression tests.
6. **Document baseline flow in code comments + README notes** (**2h, Completed by Scordo**)
   - Description: Add code-level references pointing to the baseline flow and planner UI touchpoints.
   - Preconditions: Microtasks 1–3.
   - Parallel affinities: Documentation (Story 6).
   - Output artifact: Inline comments + doc link.

---

## Story 2 — AI Agent Communication & TOON/JSON Protocol Design (Direct Prompting)
**Goal**: Define the AI agent communication protocol, context delivery strategy, and TOON artifacts used for scheduling and feedback.

**Functional Scope**
- Identify AI agent APIs and integration constraints.
- Define **system knowledge** payloads to pass as **context/knowledge base** (not prompt).
- Define dual-channel communication: **`.toon` from system to agent** and **`.json` from agent to system**.
- Define TOON-based scheduling file and JSON response contract boundaries.
- Plan for **direct prompting with context** (no MCP/RLM in this version).

**Estimated Effort**: **16 hours** (5 completed microtasks × 2h, 3 dev microtasks × 2h)

**Dependencies**
- Story 1 (baseline flow understanding).

**Expected Deliverables**
- AI communication specification (context structure, TOON request + JSON response schemas, error handling).
- Security + privacy considerations for data transfer (GDPR data minimization).

**Sprint Timing**: **Early sprint → before mid-sprint** (enables backend + UI work).

**Microtasks**
1. **AI API inventory + endpoint research** (**2h, Completed by Capone**)
   - Description: Identify Gemini Gemma and Grok Llama-70B endpoints (if available), payload limits, auth, and supported formats; search public docs for APIs/SDKs and record findings for implementation handoff.
   - Preconditions: Story 1 complete.
   - Parallel affinities: None.
   - Output artifact: AI API assumptions list + endpoint/SDK discovery notes with URLs and version details.
2. **Define system knowledge base payload** (**2h, Completed by Cantarini**)
   - Description: Specify required system info, constraints, decision metrics, and historic schedule context to pass as a knowledge base.
   - Preconditions: Story 1, Microtask 1.
   - Parallel affinities: Metrics design (Story 3).
   - Output artifact: Knowledge base schema draft.
3. **TOON request + JSON response specification** (**2h, Completed by Scordo**)
   - Description: Define `.toon` schedule input format sent from system to the agent and `.json` response format for AI-produced schedules and metadata.
   - Preconditions: Story 1, Microtask 2.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: TOON request schema section + JSON response schema section.
4. **Define communication protocol & instructions** (**2h, Completed by Cantone, Capone**)
   - Description: Specify request/response flow, retries, timeouts, and agent execution instructions (direct prompting with `.toon` input; response strictly `.json`).
   - Preconditions: Microtasks 1–3.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Protocol flow chart and error taxonomy.
5. **GDPR/data minimization review** (**2h, Completed by Cantone, Capone**)
   - Description: Ensure only necessary doctor data and scheduling constraints are transmitted; define redaction rules.
   - Preconditions: Microtasks 2–4.
   - Parallel affinities: Documentation (Story 6).
   - Output artifact: Data minimization checklist.
6. **Implement TOON serialization module** (**2h, Completed by Capone**)
   - Description: Build code to generate `.toon` payloads from the scheduling model with validation hooks.
   - Preconditions: Microtasks 2–4.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: TOON builder + validation unit tests.
7. **Implement JSON response parsing + validation** (**2h, Completed by Cappellini**)
   - Description: Build JSON parsing layer that maps AI schedules to internal models with strict schema checks.
   - Preconditions: Microtasks 3–4.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: JSON parser + validation tests.
8. **Implement AI client adapter (Gemma + Llama-70B)** (**2h, Completed by Cantone**)
   - Description: Implement configurable AI client interface and adapters for target models with timeout/retry handling.
   - Preconditions: Microtasks 1–4.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: AI client adapter + mockable interface.

---

## Story 3 — Metrics & Decision Framework (GQM+Strategy Integration)
**Goal**: Integrate the provided GQM+Strategy analysis into the decision metrics, define the multidimensional priority scale, and specify the decision algorithm for comparing 4 schedules (1 standard + 3 AI).

**Functional Scope**
- Integrate GQM+Strategy metrics and measurement plan into scheduler comparison.
- Define a **multidimensional priority scale** and decision algorithm to select among the 4 schedules.
- Model for summarizing schedule comparison data and selection rationale.

**Estimated Effort**: **18 hours** (2 completed microtasks × 2h, 5 dev microtasks totaling 14h)

**Dependencies**
- Story 1 (constraints + data model).
- Story 2 (knowledge base + AI protocol impacts).

**Expected Deliverables**
- Decision metrics catalog aligned with GQM+Strategy levels (business/software/operational).
- Priority scale and decision algorithm specification.
- API contract draft for comparison and selection.

**Sprint Timing**: **Early sprint → before mid-sprint** (so UI can target final metrics).

**Microtasks**
1. **Integrate GQM+Strategy metrics into planner metrics list** (**2h, Completed by Cantarini, Scordo**)
   - Description: Translate Business/Software/Operational goals into planner-visible comparison metrics.
   - Preconditions: Story 1.
   - Parallel affinities: None.
   - Output artifact: GQM-aligned metrics list + definitions.
2. **Map data sources for each metric** (**2h, Completed by Cantarini, Scordo**)
   - Description: Map required data (constraints, preferences, shift assignments, feedback) to each GQM metric.
   - Preconditions: Microtask 1.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Data-to-metric matrix.
3. **Implement metric aggregation + normalization utilities** (**3h, Completed by Cantone, Capone, Scordo**)
   - Description: Code metric calculations, scaling, and normalization functions based on the defined formulas.
   - Preconditions: Microtasks 1–2.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Metric calculation utilities + unit tests.
4. **Implement multidimensional priority scale config** (**3h, Completed by Cantone, Capone, Scordo**)
   - Description: Encode weighting/priority rules as configuration with defaults and override support.
   - Preconditions: Microtasks 1–3.
   - Parallel affinities: None.
   - Output artifact: Priority scale config + validation.
5. **Implement decision algorithm service** (**3h, Completed by Capone**)
   - Description: Build the algorithm that selects the preferred schedule using priority scale inputs.
   - Preconditions: Microtasks 3–4.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Decision service + unit tests.
6. **Implement comparison payload builder** (**3h, Completed by Capone**)
   - Description: Build backend models and serialization for the 4-schedule comparison response.
   - Preconditions: Microtasks 3–5.
   - Parallel affinities: UI story (Story 4).
   - Output artifact: Comparison payload DTOs + mapper.
7. **Add audit + validation checks for metric computation** (**2h**)
   - Description: Implement QA checks for missing metrics and audit annotations for selection outcomes.
   - Preconditions: Microtasks 3–6.
   - Parallel affinities: Documentation (Story 6).
   - Output artifact: Audit validation module.

---

## Story 4 — Planner UI: Schedule Comparison & Selection
**Goal**: Design the planner UI experience for comparing 4 schedules with decision metrics and selecting a preferred schedule.

**Functional Scope**
- Loading modal for schedule generation.
- Display of **only decision metrics** for each schedule.
- Schedule selection buttons + confirmation pop-up.
- Reuse existing success message patterns.

**Estimated Effort**: **10 hours** (5 dev microtasks × 2h)

**Dependencies**
- Story 3 (metrics schema).
- Story 1 (current UI surfaces).

**Expected Deliverables**
- UI wireframes or flow diagram.
- UI component breakdown (data bindings, states, error cases).

**Sprint Timing**: **Before mid-sprint** (so UI concept can be demoed).

**Microtasks**
1. **Implement UI states & flows** (**2h, Completed Scordo**)
   - Description: Implement loading, success, failure, and partial schedule states in the planner UI.
   - Preconditions: Story 1.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Planner UI state components.
2. **Implement comparison layout for 4 schedules** (**2h, in progress Cantarini**)
   - Description: Build the layout for four schedule cards showing decision metrics only.
   - Preconditions: Story 3, Microtask 6.
   - Parallel affinities: None.
   - Output artifact: Comparison layout components.
3. **Implement selection + confirmation modal** (**2h**)
   - Description: Implement confirmation copy and selection behaviors for planner selection.
   - Preconditions: Microtask 2.
   - Parallel affinities: Documentation (Story 6).
   - Output artifact: Modal component + copy.
4. **Wire success message reuse** (**2h**)
   - Description: Integrate existing success notification patterns for completed selection flow.
   - Preconditions: Story 1 UI inventory.
   - Parallel affinities: None.
   - Output artifact: Success notification wiring.
5. **Implement UI error + fallback handling** (**2h**)
   - Description: Implement error handling for missing metrics or AI failures (UI fallbacks + messages).
   - Preconditions: Story 2 protocol.
   - Parallel affinities: Backend orchestration (Story 5).
   - Output artifact: Error handling UI components.

---

## Story 5 — Backend Orchestration Plan for AI Rescheduling
**Goal**: Define backend orchestration steps for generating 1 standard + 3 AI schedules, computing metrics, and exposing comparison + selection endpoints.

**Functional Scope**
- Generation pipeline orchestration and sequencing.
- AI schedule ingestion using **TOON input** and **JSON output** parsing/unmarshalling.
- Metrics computation and storage.
- Selection endpoint behavior.

**Estimated Effort**: **10 hours** (6 dev microtasks totaling 10h)

**Dependencies**
- Story 2 (AI protocol + TOON).
- Story 3 (metrics spec).

**Expected Deliverables**
- Backend orchestration plan with endpoint list and sequence diagram.
- Error handling + fallback strategy.

**Sprint Timing**: **Before mid-sprint** (to enable demoable architecture).

**Microtasks**
1. **Implement orchestration sequence service** (**2h**)
   - Description: Implement the service that sequences standard + AI schedule generation and metrics computation.
   - Preconditions: Stories 2–3.
   - Parallel affinities: UI state design (Story 4).
   - Output artifact: Orchestration service implementation.
2. **Implement TOON request generation + validation** (**2h, Completed by Cantone**)
   - Description: Implement `.toon` request generation and validation gates before AI calls.
   - Preconditions: Story 2.
   - Parallel affinities: None.
   - Output artifact: TOON generation + validation pipeline.
3. **Implement JSON response ingestion + validation** (**2h, Completed by Scordo**)
   - Description: Implement AI schedule parsing, validation, and conversion to internal models.
   - Preconditions: Story 2.
   - Parallel affinities: None.
   - Output artifact: JSON ingestion pipeline.
4. **Implement comparison + selection endpoints** (**2h**)
   - Description: Build API endpoints for comparison metrics and committing planner selection.
   - Preconditions: Story 3, Microtask 6.
   - Parallel affinities: UI story (Story 4).
   - Output artifact: API endpoints + integration tests.
5. **Implement error + retry handling** (**1h**)
   - Description: Implement failure handling and retries for AI calls and metric computation.
   - Preconditions: Story 2 protocol.
   - Parallel affinities: UI error design (Story 4).
   - Output artifact: Error handling logic + logging.
6. **Implement security + audit logging** (**1h**)
   - Description: Implement audit logging for schedule selection and AI decisions.
   - Preconditions: Story 3, Microtask 7.
   - Parallel affinities: Documentation (Story 6).
   - Output artifact: Audit logging module.

---

## Story 6 — Documentation & Stakeholder Materials
**Goal**: Produce documentation, sprint summary materials, and stakeholder presentation assets.

**Functional Scope**
- Technical documentation for new flows, AI integration, and metrics logic.
- Sprint documentation (what was done, decisions made).
- Slides for stakeholder presentation.
- Retro input artifacts.

**Estimated Effort**: **4 hours** (4 microtasks × 1h)

**Dependencies**
- Stories 1–5 (for accurate content).

**Expected Deliverables**
- Technical docs for AI rescheduling flows.
- Sprint summary + retrospective input.
- Stakeholder slide deck.

**Sprint Timing**: **After mid-sprint → end of sprint** (final packaging + review).

**Microtasks**
1. **Technical documentation draft** (**1h**)
   - Description: Document new flow diagrams, AI protocol, and metrics logic.
   - Preconditions: Stories 2–5.
   - Parallel affinities: None.
   - Output artifact: Technical doc draft.
   - **Nanotasks (parallelizable)**:
     - **6.1.a** Capture backend orchestration flow diagram draft (sequence: standard + AI schedules, metrics, selection) and stash in doc workspace.
     - **6.1.b** Draft AI protocol section (TOON request, JSON response, retries/timeouts, error taxonomy).
     - **6.1.c** Draft metrics + decision algorithm section (GQM mapping, priority scale, comparison payload fields).
     - **6.1.d** Draft UI comparison/selection flow notes (states, error handling, success messaging).
     - **6.1.e** Assemble glossary + data minimization notes (GDPR redactions, data fields included/excluded).
2. **Sprint summary + decisions log** (**1h**)
   - Description: Write a sprint summary and major decision record.
   - Preconditions: Story 1–5.
   - Parallel affinities: None.
   - Output artifact: Sprint summary doc.
   - **Nanotasks (parallelizable)**:
     - **6.2.a** Write sprint timeline summary (what completed per story, key milestones).
     - **6.2.b** Capture decisions log (AI models chosen, protocol choices, metric priorities).
     - **6.2.c** Record open questions/risks and mitigation items for next sprint.
     - **6.2.d** Add dependency impacts (what Story 6 content depends on remaining Story 4/5 items).
3. **Stakeholder slide deck** (**1h**)
   - Description: Create slides covering problem, approach, and progress.
   - Preconditions: Story 4 design and Story 5 orchestration plan.
   - Parallel affinities: None.
   - Output artifact: Slide deck outline or draft.
   - **Nanotasks (parallelizable)**:
     - **6.3.a** Draft slide 1–2: problem statement + baseline scheduling flow snapshot.
     - **6.3.b** Draft slide 3–4: AI rescheduling approach (protocol + orchestration diagram).
     - **6.3.c** Draft slide 5: metrics + decision framework summary.
     - **6.3.d** Draft slide 6: UI comparison/selection preview + next steps.
4. **Retrospective inputs** (**1h**)
   - Description: Capture what went well, what didn’t, and risks for next sprint.
   - Preconditions: Story 6 Microtasks 1–3.
   - Parallel affinities: None.
   - Output artifact: Retro notes.
   - **Nanotasks (parallelizable)**:
     - **6.4.a** Collect “what went well” bullets (process, technical wins, collaboration).
     - **6.4.b** Collect “what didn’t go well” bullets (blockers, gaps, late changes).
     - **6.4.c** Capture risks + mitigations for next sprint (AI reliability, data quality, UI readiness).
     - **6.4.d** Note improvement actions (documentation parallelization, testing strategy, stakeholder sync).

---

## Cross-Story Dependency Highlights
- **Story 1 → Stories 2–5**: Baseline scheduling flow informs AI integration, metrics design, UI, and orchestration.
- **Story 2 → Stories 3–5**: AI protocol and TOON specification drive metrics inputs and backend orchestration.
- **Story 3 → Story 4**: UI depends on finalized decision metrics and comparison payload schema.
- **Story 5 → Story 4**: Error/timeout behavior influences UI loading and failure states.
- **Stories 1–5 → Story 6**: Documentation and stakeholder materials depend on completed analysis/design outputs.

---

## Timeline View (Narrative)
- **Week 1 (Early sprint)**: Complete Story 1. Start Story 2 (AI protocol) and Story 3 (metrics). Begin Story 4 UI concepting in parallel once metrics schema is drafted.
- **Week 2 (Before mid-sprint meeting)**: Finalize Stories 2–5 design outputs (including TOON/JSON protocol and decision algorithm) so that UI wireframes and backend orchestration plan can be demoed.
- **After mid-sprint alignment (Week 2)**: Focus on Story 6 documentation + stakeholder materials using finalized artifacts.

---

## Expected Outcomes
### Mid-sprint (Alignment Meeting)
- Baseline flow map (Story 1).
- AI communication + TOON protocol draft (Story 2).
- Decision metrics list + comparison schema (Story 3).
- UI wireframes + selection flow (Story 4).
- Backend orchestration plan (Story 5).

### End of Sprint (Review + Retro)
- Full technical documentation, sprint summary, and stakeholder slide deck (Story 6).
- Consolidated roadmap with dependency-consistent story and microtask plan.

---

## Effort Summary
- Story 1: 12h
- Story 2: 16h
- Story 3: 18h
- Story 4: 10h
- Story 5: 10h
- Story 6: 4h
- **Total**: **70 hours**
