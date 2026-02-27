package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SystemUserDAO extends JpaRepository<SystemUser, Long> {
    @Query("SELECT u FROM SystemUser u " +
            "WHERE u.email = :email " +
            "AND u.password = :password " +
            "AND u.id = (SELECT MIN(u2.id) FROM SystemUser u2 WHERE u2.email = :email AND u2.password = :password)")
    SystemUser findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Query("SELECT u FROM SystemUser u " +
            "WHERE u.email = :email " +
            "AND u.id = (SELECT MIN(u2.id) FROM SystemUser u2 WHERE u2.email = :email)")
    SystemUser findByEmail(@Param("email") String email);

    @Query("SELECT u FROM SystemUser u " +
            "WHERE u.email = :email " +
            "AND u.tenant = :tenant " +
            "AND u.id = (SELECT MIN(u2.id) FROM SystemUser u2 WHERE u2.email = :email AND u2.tenant = :tenant)")
    SystemUser findByEmailAndTenant(@Param("email") String email, @Param("tenant") String tenant);

    SystemUser findById(long userID);

}
