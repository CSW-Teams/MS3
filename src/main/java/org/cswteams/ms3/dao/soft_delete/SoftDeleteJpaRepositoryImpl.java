package org.cswteams.ms3.dao.soft_delete;

import org.cswteams.ms3.entity.soft_delete.SoftDeletableEntity;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link SoftDeleteJpaRepository} that provides soft delete functionality.
 *
 * <p>This class extends {@link SimpleJpaRepository} and overrides delete-related methods
 * to mark entities as deleted instead of permanently removing them from the database.
 * Additionally, it provides a method to restore soft-deleted entities.</p>
 *
 * <p>Entities must implement {@link SoftDeletableEntity} to support the soft delete feature.
 * If an entity does not implement this interface, the default delete behavior is applied.</p>
 *
 * @param <T>  the type of the entity
 * @param <ID> the type of the entity's identifier
 */
public class SoftDeleteJpaRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements SoftDeleteJpaRepository<T, ID> {

    /** The {@link EntityManager} used for persistence operations. */
    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * Constructs a new instance of {@code SoftDeleteJpaRepositoryImpl}.
     *
     * @param entityInformation metadata about the entity type
     * @param entityManager     the {@link EntityManager} used for persistence operations
     */
    public SoftDeleteJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    /**
     * Marks an entity as deleted by setting its {@code deleted} flag to {@code true}.
     * If the entity does not implement {@link SoftDeletableEntity}, the default delete
     * behavior is applied.
     *
     * @param entity the entity to delete
     */
    @Override
    @Transactional
    public void delete(T entity) {
        if (entity instanceof SoftDeletableEntity) {
            ((SoftDeletableEntity) entity).setDeleted(true);
            entityManager.merge(entity);
        }  else {
            super.delete(entity);
        }
    }

    /**
     * Marks an entity as deleted by its ID.
     * If the entity does not implement {@link SoftDeletableEntity}, the default delete
     * behavior is applied.
     *
     * @param id the ID of the entity to delete
     * @throws IllegalArgumentException if the entity is not found by the given ID
     */
    @Override
    @Transactional
    public void deleteById(ID id) {
        Optional<T> entityOpt = findById(id);

        if (entityOpt.isPresent()) {
            T entity = entityOpt.get();
            if (entity instanceof SoftDeletableEntity) {
                ((SoftDeletableEntity) entity).setDeleted(true);
                entityManager.merge(entity);
            } else {
                super.deleteById(id);
            }
        } else {
            throw new IllegalArgumentException("Entity not found for ID: " + id);
        }
    }

    /**
     * Marks all entities as deleted by setting their {@code deleted} flag to {@code true}.
     * If the entities do not implement {@link SoftDeletableEntity}, the default delete
     * behavior is applied.
     */
    @Override
    @Transactional
    public void deleteAll() {
        List<T> entities = findAll();

        if (!(entities instanceof SoftDeletableEntity)) {
            super.deleteAll();

            return;
        }

        for (T entity : entities) {
            ((SoftDeletableEntity) entity).setDeleted(true);
            entityManager.merge(entity);
        }
    }

    /**
     * Marks all provided entities as deleted by setting their {@code deleted} flag to {@code true}.
     * If the entities do not implement {@link SoftDeletableEntity}, the default delete
     * behavior is applied.
     *
     * @param entities the entities to delete
     */
    @Override
    @Transactional
    public void deleteAll(Iterable<? extends T> entities) {
        if (!(entities instanceof SoftDeletableEntity)) {
            super.deleteAll(entities);
        }

        for (T entity : entities) {
            ((SoftDeletableEntity) entity).setDeleted(true);
            entityManager.merge(entity);
        }
    }

    /**
     * Restores an entity that was soft deleted by its ID.
     *
     * @param id the ID of the entity to restore
     * @throws IllegalArgumentException if the entity is not found by the given ID
     *                                  or does not support soft delete
     */
    @Override
    public void restoreById(ID id) {
        Optional<T> entityOpt = findById(id);

        if (entityOpt.isPresent()) {
            T entity = entityOpt.get();

            if (entity instanceof SoftDeletableEntity) {
                ((SoftDeletableEntity) entity).setDeleted(false);
                entityManager.merge(entity);
            } else {
                throw new IllegalArgumentException("Entity does not support soft delete");
            }
        } else {
            throw new IllegalArgumentException("Entity not found by ID: " + id);
        }
    }
}