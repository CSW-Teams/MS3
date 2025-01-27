package org.cswteams.ms3.config.soft_delete;

import org.aspectj.lang.JoinPoint;
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

//    /**
//     * Pointcut for all methods in the REST layer.
//     */
//    @Pointcut("execution(* org.cswteams.ms3.rest..*(..))")
//    public void restLayer() {}
//
//    /**
//     * Pointcut for all methods in the control layer.
//     */
//    @Pointcut("execution(* org.cswteams.ms3.control..*(..))")
//    public void controlLayer() {}
//
//    /**
//     * Pointcut for all methods in the DAO layer.
//     */
//    @Pointcut("execution(* org.cswteams.ms3.dao..*(..))")
//    public void daoLayer() {}
//
//    /**
//     * Advice to enable the soft delete filter before executing methods in the REST, control, or DAO layers,
//     * unless the method is annotated with {@link org.cswteams.ms3.config.annotations.DisableSoftDelete}.
//     */
//    @Before("(restLayer() || controlLayer() || daoLayer()) && !disableSoftDeleteMethods()")
//    public void enableSoftDeleteFilter(JoinPoint joinPoint) {
//        String filterName = "softDeleteFilter";
//
//        Map<String, Object> filterParams = new HashMap<>();
//        filterParams.put("deleted", false);
//
//        softDeleteService.enableSoftDeleteFilter(filterName, filterParams);
//    }

////    @Pointcut("@annotation(org.cswteams.ms3.config.annotations.SoftDeletable)")
//    @Pointcut("execution(* org.springframework.data.jpa.repository.JpaRepository+.find*(..))")
//    public void softDeletableAnnotation() {}
//
//    @Before("softDeletableAnnotation() && !disableSoftDeleteMethods()")
//    public void enableSoftDeleteFilter(JoinPoint joinPoint) {
//        String filterName = "softDeleteFilter";
//
//        Map<String, Object> filterParams = new HashMap<>();
//        filterParams.put("deleted", false);
//
//        softDeleteService.enableSoftDeleteFilter(filterName, filterParams);
//    }

    /**
     * Advice to disable the soft delete filter before executing methods annotated with
     * {@link org.cswteams.ms3.config.annotations.DisableSoftDelete}.
     *
     * @param joinPoint The join point representing the intercepted method.
     */
    @Before("disableSoftDeleteMethods()")
    public void disableSoftDeleteFilter(JoinPoint joinPoint) {
        String filterName = "softDeleteFilter";
        softDeleteService.disableSoftDeleteFilter(filterName);
    }
}