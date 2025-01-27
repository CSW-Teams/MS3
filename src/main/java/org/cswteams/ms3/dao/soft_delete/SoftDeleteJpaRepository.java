package org.cswteams.ms3.dao.soft_delete;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * A custom base repository interface for entities that require soft delete functionality.
 *
 * <p>This repository extends {@link JpaRepository} and overrides delete-related methods to
 * ensure compatibility with soft delete logic. It also adds a method to restore entities
 * by their ID.</p>
 *
 * <p>Annotations used:</p>
 * <ul>
 *     <li>{@code @NoRepositoryBean} - Indicates that this interface is not a candidate for
 *     Spring Data repository instantiation and should be extended by concrete repositories.</li>
 * </ul>
 *
 * @param <T>  the entity type
 * @param <ID> the type of the entity's identifier
 */
@NoRepositoryBean
public interface SoftDeleteJpaRepository<T, ID> extends JpaRepository<T, ID> {
    /**
     * Deletes an entity. This method is overridden to support soft delete functionality.
     *
     * @param t the entity to delete
     */
    @Override
    void delete(T t);

    /**
     * Deletes an entity by its identifier. This method is overridden to support
     * soft delete functionality.
     *
     * @param id the identifier of the entity to delete
     */
    @Override
    void deleteById(ID id);

    /**
     * Deletes all entities. This method is overridden to support soft delete functionality.
     */
    @Override
    void deleteAll();

    /**
     * Deletes all the entities in the given iterable. This method is overridden to
     * support soft delete functionality.
     *
     * @param entities the entities to delete
     */
    @Override
    void deleteAll(Iterable<? extends T> entities);

    /**
     * Restores an entity that was soft deleted by its identifier.
     *
     * @param id the identifier of the entity to restore
     */
    void restoreById(ID id);
}