package org.cswteams.ms3.dao.soft_delete;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SoftDeleteJpaRepository<T, ID> extends JpaRepository<T, ID> {
    @Override
    void delete(T t);

    @Override
    void deleteById(ID id);

    @Override
    void deleteAll();

    @Override
    void deleteAll(Iterable<? extends T> entities);

    void restoreById(ID id);
}