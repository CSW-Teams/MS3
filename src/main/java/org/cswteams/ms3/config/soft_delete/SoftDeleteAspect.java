package org.cswteams.ms3.config.soft_delete;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Aspect to manage the activation and deactivation of the soft delete filter.
 * This aspect intercepts methods in specific layers (REST, control, DAO) and automatically
 * enables or disables the soft delete filter depending on the context.
 */
@Aspect
@Component
public class SoftDeleteAspect {

    @Autowired
    private SoftDeleteService softDeleteService;

    /**
     * Pointcut for methods annotated with {@link org.cswteams.ms3.config.annotations.DisableSoftDelete}.
     * These methods will bypass the soft delete filter.
     */
    @Pointcut("@annotation(org.cswteams.ms3.config.annotations.DisableSoftDelete)")
    public void disableSoftDeleteMethods() {}

    /**
     * Advice to disable the soft delete filter before executing methods annotated with
     * {@link org.cswteams.ms3.config.annotations.DisableSoftDelete}.
     */
    @Before("disableSoftDeleteMethods()")
    public void disableSoftDeleteFilter() {
        String filterName = "softDeleteFilter";
        softDeleteService.disableSoftDeleteFilter(filterName);
    }
}