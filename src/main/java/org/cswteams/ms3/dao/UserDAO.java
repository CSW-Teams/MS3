package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Long> {
    User findByEmailAndPassword(String email, String password);

    User findByEmail(String email);

    User findById(long userID);

}
