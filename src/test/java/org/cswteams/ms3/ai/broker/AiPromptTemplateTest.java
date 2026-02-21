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
        assertTrue(systemPrompt.contains("Every minimum value in `hard_coverage_requirements` (`structured`, `specialist_junior`, `specialist_senior`, and `total`) is mandatory"));
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
                + "TOON_INPUT:\n"
                + "ctx:{p:\"2026-05-20/2026-05-22\",m:\"generate\",hv:2}";

        assertEquals(expectedSnapshot, userContent);
    }
}
