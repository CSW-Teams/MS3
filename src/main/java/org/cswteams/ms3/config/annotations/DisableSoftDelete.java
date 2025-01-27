package org.cswteams.ms3.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to disable the Hibernate SoftDelete filter for a specific method.
 *
 * <p>When applied to a method, this annotation ensures that soft-deleted entities
 * are included in the query results. It can be used to override the default
 * behavior of filtering out soft-deleted entities.</p>
 *
 * <p>This annotation is intercepted by a Spring Aspect, which disables the
 * Hibernate soft delete filter before the execution of the annotated method.</p>
 *
 * <p><b>Usage:</b></p>
 * <pre>
 * &#64;DisableSoftDelete
 * public List<Entity> method() {
 *     // Implementation
 * }
 * </pre>
 *
 * <p><b>Implementation Details:</b></p>
 * <p>An {@link org.aspectj.lang.annotation.Aspect} is used to capture this annotation.
 * The aspect invokes {@code SoftDeleteService.disableSoftDeleteFilter()} to
 * disable the Hibernate filter for the duration of the method execution.</p>
 *
 * @see org.hibernate.annotations.Filter
 * @see org.hibernate.annotations.FilterDef
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DisableSoftDelete {
}