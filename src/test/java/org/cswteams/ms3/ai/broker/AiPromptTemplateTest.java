package org.cswteams.ms3.ai.broker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiPromptTemplateTest {

    @Test
    void systemPromptContainsNormativeDslAndHolidaySemanticsSections() {
        String systemPrompt = AiPromptTemplate.systemPrompt();

        assertTrue(systemPrompt.contains("TOON_INPUT DSL LEGEND (NORMATIVE):"));
        assertTrue(systemPrompt.contains("1) Context header"));
        assertTrue(systemPrompt.contains("2) Shift catalog"));
        assertTrue(systemPrompt.contains("3) Doctor entries"));
        assertTrue(systemPrompt.contains("4) Active constraints"));
        assertTrue(systemPrompt.contains("Invalid-input policy (NORMATIVE):"));
        assertTrue(systemPrompt.contains("HOLIDAY SEMANTICS (NORMATIVE):"));
        assertTrue(systemPrompt.contains("Token: ctx:{p,m,hv?}"));
        assertTrue(systemPrompt.contains("Token: sh[n]{i,s,d,u,rs,rj}"));
        assertTrue(systemPrompt.contains("Token: dr[n]"));
        assertTrue(systemPrompt.contains("h[n]{id,s,e,tz?} (required)"));
        assertTrue(systemPrompt.contains("`2` means doctor holidays are emitted using the structured `h[n]{id,s,e,tz?}` block"));
        assertTrue(systemPrompt.contains("`sh[].rj` maps only to SPECIALIST_JUNIOR"));
        assertTrue(systemPrompt.contains("hard_coverage_requirements[n]{shift_id,structured,specialist_junior,specialist_senior,total}"));
        assertTrue(systemPrompt.contains("role_validation_scratchpad[n]{shift_id,required_role,required_count,candidate_doctor_ids}"));
        assertTrue(systemPrompt.contains("Backend-provided scratchpad entries override any role-candidate inference from `dr`"));
        assertTrue(systemPrompt.contains("Every minimum value in `hard_coverage_requirements` (`structured`, `specialist_junior`, `specialist_senior`, and `total`) is mandatory"));
        assertTrue(systemPrompt.contains("required ON_CALL role coverage is exactly equal to the same per-role minima declared in `hard_coverage_requirements`"));
        assertTrue(systemPrompt.contains("valid solution must satisfy both assignment layers for each `shift_id` and role — ON_DUTY meets minima and ON_CALL meets the same minima"));
        assertTrue(systemPrompt.contains("for every `shift_id`, generate assignments for both `assignment_status` layers: `ON_DUTY` and `ON_CALL`"));
        assertTrue(systemPrompt.contains("per-role minima apply independently to each `assignment_status` layer"));
        assertTrue(systemPrompt.contains("the same `doctor_id` cannot appear twice for the same `shift_id` across different `assignment_status` values"));
        assertTrue(systemPrompt.contains("Concrete coverage-layer example for `S_73_20260223`"));
        assertTrue(systemPrompt.contains("{\"shift_id\":\"S_73_20260223\",\"doctor_id\":101,\"role_covered\":\"STRUCTURED\",\"assignment_status\":\"ON_DUTY\"}"));
        assertTrue(systemPrompt.contains("{\"shift_id\":\"S_73_20260223\",\"doctor_id\":202,\"role_covered\":\"STRUCTURED\",\"assignment_status\":\"ON_CALL\"}"));
        assertTrue(systemPrompt.contains("Read the provided preprocessed `role_validation_scratchpad` rows."));
        assertTrue(systemPrompt.contains("only source of truth of allowed doctor IDs"));
        assertTrue(systemPrompt.contains("STRICTLY FORBIDDEN from assigning a doctor_id outside the listed `candidate_doctor_ids`"));
        assertTrue(!systemPrompt.contains("scan the `TOON_INPUT` doctor list (`dr[...]`)"));
        assertTrue(!systemPrompt.contains("Populate `candidate_doctor_ids` with these matching IDs only."));
        assertTrue(systemPrompt.contains("spec ID: " + AiPromptTemplate.metricsSpecId()));
    }

    @Test
    void buildUserContentEmitsNormativeRulesBeforeToonInputPayload() {
        String userContent = AiPromptTemplate.buildUserContent(
                "Prefer BALANCED variant",
                "ctx:{p:\"2026-05-20/2026-05-22\",m:\"generate\",hv:2}"
        );

        String expectedSnapshot = "Instructions:\n"
                + "Prefer BALANCED variant\n\n"
                + "Vocabulary rule: TOON_INPUT doctors[*].role uses the same enum naming as assignments[*].role_covered (STRUCTURED, SPECIALIST_JUNIOR, SPECIALIST_SENIOR).\n\n"
                + "Holiday rule: in COMPACT mode doctor holidays use `h[n]{id,s,e,tz?}` rows (id optional, s/e inclusive dates, tz optional quoted metadata).\n\n"
                + "Scratchpad authority rule: if TOON_INPUT includes `role_validation_scratchpad[...]`, treat backend-provided `candidate_doctor_ids` as authoritative for each `(shift_id, role_required)` pair.\n\n"
                + "Coverage checklist rule: if TOON_INPUT includes `hard_coverage_checklist[...]`, treat every checklist item as mandatory and machine-checkable.\n\n"
                + "TOON_INPUT:\n"
                + "ctx:{p:\"2026-05-20/2026-05-22\",m:\"generate\",hv:2}";

        assertEquals(expectedSnapshot, userContent);
    }

    @Test
    void buildUserContentKeepsAppendedScratchpadSectionInPayload() {
        String toonPayload = "ctx:{p:\"2026-05-20/2026-05-22\",m:\"generate\",hv:2}\n"
                + "role_validation_scratchpad[1]{shift_id,required_role,required_count,candidate_doctor_ids}:"
                + "{shift_id:\"S_101_20260520\",required_role:STRUCTURED,required_count:1,candidate_doctor_ids:[11,12]}";

        String userContent = AiPromptTemplate.buildUserContent("Prefer BALANCED variant", toonPayload);

        assertTrue(userContent.contains("Scratchpad authority rule: if TOON_INPUT includes `role_validation_scratchpad[...]`"));
        assertTrue(userContent.contains("Coverage checklist rule: if TOON_INPUT includes `hard_coverage_checklist[...]`"));
        assertTrue(userContent.contains("role_validation_scratchpad[1]{shift_id,required_role,required_count,candidate_doctor_ids}"));
        assertTrue(!userContent.contains("scan the `TOON_INPUT` doctor list (`dr[...]`)"));
    }
}
