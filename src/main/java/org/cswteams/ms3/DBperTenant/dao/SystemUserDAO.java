package org.cswteams.ms3.DBperTenant.dao;

import org.cswteams.ms3.DBperTenant.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemUserDAO extends JpaRepository<SystemUser, Long> {
    SystemUser findByEmail(String email);

}