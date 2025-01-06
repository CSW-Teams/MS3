package org.cswteams.ms3.DBperTenant.dao;

import org.cswteams.ms3.DBperTenant.entity.TenantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantUserDAO extends JpaRepository<TenantUser, Long> {
    TenantUser findByEmail(String email);
}