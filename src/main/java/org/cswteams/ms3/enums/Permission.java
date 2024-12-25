package org.cswteams.ms3.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    DOCTOR_GET("doctor:read"),
    DOCTOR_PUT("doctor:put"),
    DOCTOR_POST("doctor:post"),
    DOCTOR_DELETE("doctor:delete"),

    PLANNER_GET("planner:read"),
    PLANNER_PUT("planner:put"),
    PLANNER_POST("planner:post"),
    PLANNER_DELETE("planner:delete"),

    CONFIGURATOR_GET("configurator:read"),
    CONFIGURATOR_PUT("configurator:put"),
    CONFIGURATOR_POST("configurator:post"),
    CONFIGURATOR_DELETE("configurator:delete");

    @Getter
    private final String permission;
}
