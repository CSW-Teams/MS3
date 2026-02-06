package org.cswteams.ms3.ai.broker.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AiScheduleVariantsResponse {

    private final Map<String, AiScheduleResponse> variants;

    public AiScheduleVariantsResponse(Map<String, AiScheduleResponse> variants) {
        if (variants == null || variants.isEmpty()) {
            this.variants = Collections.emptyMap();
        } else {
            this.variants = Collections.unmodifiableMap(new LinkedHashMap<>(variants));
        }
    }

    public Map<String, AiScheduleResponse> getVariants() {
        return variants;
    }

    public AiScheduleResponse getVariant(String label) {
        return variants.get(label);
    }
}
