package org.cswteams.ms3.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.cswteams.ms3.entity.TenantUser;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantUserDAO extends JpaRepository<TenantUser, Long> {
    TenantUser findByEmailAndPassword(String email, String password);

    TenantUser findByEmail(String email);

    TenantUser findById(long userID);

}