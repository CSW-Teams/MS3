package org.cswteams.ms3.dao.soft_delete;

import org.cswteams.ms3.entity.soft_delete.SoftDeletableEntity;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

public class SoftDeleteJpaRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements SoftDeleteJpaRepository<T, ID> {

    @PersistenceContext
    private final EntityManager entityManager;

    public SoftDeleteJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

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

    @Override
    @Transactional
    public void deleteById(ID id) {
        // Recupera l'entità dal database
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

    @Override
    @Transactional
    public void deleteAll() {
        // Trova tutte le entità nel repository
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

    @Override
    public void restoreById(ID id) {
        // Recupera l'entità dal database
        Optional<T> entityOpt = findById(id);

        if (entityOpt.isPresent()) {
            T entity = entityOpt.get();

            // Controlla se l'entità è annotata con @SoftDeletable
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