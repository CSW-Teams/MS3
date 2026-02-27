package org.cswteams.ms3.control.toon;

import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class ToonActiveConstraint {
    private final ToonConstraintType type;
    private final ToonConstraintEntityType entityType;
    private final String entityId;
    private final String reason;
    private final Map<String, String> params;

    public ToonActiveConstraint(ToonConstraintType type,
                                ToonConstraintEntityType entityType,
                                String entityId,
                                String reason,
                                Map<String, String> params) {
        this.type = type;
        this.entityType = entityType;
        this.entityId = entityId;
        this.reason = reason;
        this.params = params == null ? Collections.emptyMap() : new LinkedHashMap<>(params);
    }
}
