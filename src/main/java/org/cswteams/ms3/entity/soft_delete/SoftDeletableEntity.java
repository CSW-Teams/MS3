package org.cswteams.ms3.entity.soft_delete;

import lombok.Getter;
import org.hibernate.annotations.*;

import javax.persistence.*;

/**
 * Abstract base class for entities that support soft delete functionality.
 *
 * <p>This class introduces the `is_deleted` column, which indicates whether
 * an entity has been soft-deleted. Instead of physically deleting the entity
 * from the database, it is marked as deleted by setting this flag to `true`.</p>
 *
 * <p>Soft deletion is implemented using Hibernate filters. The filter definition
 * is named `softDeleteFilter` and requires a parameter `isDeleted` of type boolean
 * to determine whether to include or exclude soft-deleted entities in query results.</p>
 *
 * <p><b>Usage:</b></p>
 * <ul>
 *   <li>To exclude soft-deleted entities from queries, enable the filter with
 *       the parameter `isDeleted` set to `false`.</li>
 *   <li>To include soft-deleted entities in queries, enable the filter with
 *       the parameter `isDeleted` set to `true`.</li>
 * </ul>
 *
 * <pre>
 * &#64;Entity
 * public class ExampleEntity extends SoftDeletableEntity {
 *     &#64;Column(name = "name")
 *     private String name;
 * }
 * </pre>
 *
 * <p><b>Annotations:</b></p>
 * <ul>
 *   <li>&#64;Getter: Automatically generates getter methods for fields.</li>
 *   <li>&#64;MappedSuperclass: Indicates that this is a base class for JPA entities.</li>
 *   <li>&#64;FilterDef: Defines a Hibernate filter named `softDeleteFilter` with a
 *       parameter `isDeleted` of type boolean.</li>
 *   <li>&#64;Filter: Applies the `softDeleteFilter` to entities that extend this class.</li>
 * </ul>
 *
 * @see org.hibernate.annotations.Filter
 * @see org.hibernate.annotations.FilterDef
 */
@Getter
@MappedSuperclass
@FilterDef(
        name = "softDeleteFilter",
        parameters = @ParamDef(name = "isDeleted", type = "boolean")
)
@Filter(name = "softDeleteFilter", condition = "is_deleted = :isDeleted")
public abstract class SoftDeletableEntity {

    /**
     * Indicates whether the entity is soft-deleted.
     *
     * <p>If {@code true}, the entity is considered deleted and can be excluded from queries
     * using the soft delete filter.</p>
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /**
     * Marks the entity as deleted or restores it by setting the {@code isDeleted} flag.
     *
     * @param deleted {@code true} to mark the entity as deleted, {@code false} to restore it.
     */
    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }
}
