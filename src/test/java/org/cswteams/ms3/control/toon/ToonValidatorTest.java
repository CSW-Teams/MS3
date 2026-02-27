package org.cswteams.ms3.control.toon;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ToonValidatorTest {

    private final ToonValidator validator = new ToonValidator();

    @Test
    void validatesLegacyPayloadWithoutOptionalSections() {
        String payload = "ctx:\n"
                + "period: \"2026-05-20/2026-05-21\"\n"
                + "mode: \"generate\"\n"
                + "shifts[1]{id,slot,date,duration,req_str,req_jun}:\n"
                + "S_1,NIGHT,2026-05-20,720,1,0\n"
                + "doctors[1]:\n"
                + "- id: 10\n";

        assertDoesNotThrow(() -> validator.postValidate(payload, ToonBuilder.SerializationMode.LEGACY));
    }

    @Test
    void validatesCompactPayloadWithoutOptionalSections() {
        String payload = "ctx:{p:\"2026-05-20/2026-05-21\",m:\"generate\"}\n"
                + "sh[1]{i,s,d,u,rs,rj}:\n"
                + "S_1,NIGHT,2026-05-20,720,1,0\n"
                + "dr[1]:\n"
                + "-i:10\n";

        assertDoesNotThrow(() -> validator.postValidate(payload, ToonBuilder.SerializationMode.COMPACT));
    }

    @Test
    void validatesOptionalConstraintSectionsForLegacyAndCompactPayloads() {
        String legacyPayload = "ctx:\n"
                + "period: \"2026-05-20/2026-05-21\"\n"
                + "mode: \"generate\"\n"
                + "shifts[1]{id,slot,date,duration,req_str,req_jun}:\n"
                + "S_1,NIGHT,2026-05-20,720,1,0\n"
                + "doctors[1]:\n"
                + "- id: 10\n"
                + "active_constraints[2]{type, entity_type, entity_id, reason, params}:\n"
                + "HARD, DOCTOR, 10, MAX_HOURS, {}\n"
                + "SOFT, SHIFT, S_1, ROLE_QUOTA, {}\n";

        String compactPayload = "ctx:{p:\"2026-05-20/2026-05-21\",m:\"generate\"}\n"
                + "sh[1]{i,s,d,u,rs,rj}:\n"
                + "S_1,NIGHT,2026-05-20,720,1,0\n"
                + "dr[1]:\n"
                + "-i:10\n"
                + "ac[2]{t,e,i,r,p}:\n"
                + "HARD,DOCTOR,10,MAX_HOURS\n"
                + "SOFT,SHIFT,S_1,ROLE_QUOTA\n";

        assertDoesNotThrow(() -> validator.postValidate(legacyPayload, ToonBuilder.SerializationMode.LEGACY));
        assertDoesNotThrow(() -> validator.postValidate(compactPayload, ToonBuilder.SerializationMode.COMPACT));
    }

    @Test
    void rejectsPayloadWhenRequiredMarkersAreMissing() {
        String missingLegacyDoctors = "ctx:\n"
                + "period: \"2026-05-20/2026-05-21\"\n"
                + "mode: \"generate\"\n"
                + "shifts[1]{id,slot,date,duration,req_str,req_jun}:\n"
                + "S_1,NIGHT,2026-05-20,720,1,0\n";

        String missingCompactDoctors = "ctx:{p:\"2026-05-20/2026-05-21\",m:\"generate\"}\n"
                + "sh[1]{i,s,d,u,rs,rj}:\n"
                + "S_1,NIGHT,2026-05-20,720,1,0\n";

        assertThrows(ToonValidationException.class,
                () -> validator.postValidate(missingLegacyDoctors, ToonBuilder.SerializationMode.LEGACY));
        assertThrows(ToonValidationException.class,
                () -> validator.postValidate(missingCompactDoctors, ToonBuilder.SerializationMode.COMPACT));
    }

    @Test
    void acceptsFeedbackCategoryReasonCode() {
        String payload = "ctx:\n"
                + "period: \"2026-05-20/2026-05-21\"\n"
                + "mode: \"generate\"\n"
                + "shifts[1]{id,slot,date,duration,req_str,req_jun}:\n"
                + "S_1,NIGHT,2026-05-20,720,1,0\n"
                + "doctors[1]:\n"
                + "- id: 10\n"
                + "feedbacks[1]{shift_id, doctor_id, reason_code, severity, comment}:\n"
                + "S_1, 10, REPEATED_WEEKDAY, 4, \"\"\n";

        assertDoesNotThrow(() -> validator.postValidate(payload, ToonBuilder.SerializationMode.LEGACY));
    }

    @Test
    void rejectsDeprecatedRemovalReasonCode() {
        String payload = "ctx:\n"
                + "period: \"2026-05-20/2026-05-21\"\n"
                + "mode: \"generate\"\n"
                + "shifts[1]{id,slot,date,duration,req_str,req_jun}:\n"
                + "S_1,NIGHT,2026-05-20,720,1,0\n"
                + "doctors[1]:\n"
                + "- id: 10\n"
                + "feedbacks[1]{shift_id, doctor_id, reason_code, severity, comment}:\n"
                + "S_1, 10, REMOVAL_REJECTED, 4, \"\"\n";

        assertThrows(ToonValidationException.class,
                () -> validator.postValidate(payload, ToonBuilder.SerializationMode.LEGACY));
    }

    @Test
    void rejectsOptionalSectionWithInvalidCount() {
        String payload = "ctx:{p:\"2026-05-20/2026-05-21\",m:\"generate\"}\n"
                + "sh[1]{i,s,d,u,rs,rj}:\n"
                + "S_1,NIGHT,2026-05-20,720,1,0\n"
                + "dr[1]:\n"
                + "-i:10\n"
                + "ac[x]{t,e,i,r,p}:\n";

        assertThrows(ToonValidationException.class,
                () -> validator.postValidate(payload, ToonBuilder.SerializationMode.COMPACT));
    }
}
