package org.cswteams.ms3.ai.comparison.domain;

public enum ScheduleCandidateType {
    STANDARD("standard"),
    EMPATHETIC("empathetic"),
    EFFICIENT("efficient"),
    BALANCED("balanced");

    private final String label;

    ScheduleCandidateType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
