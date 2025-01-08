package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemUserDAO extends JpaRepository<SystemUser, Long> {
    SystemUser findByEmailAndPassword(String email, String password);

    SystemUser findByEmail(String email);

    SystemUser findById(long userID);

}
