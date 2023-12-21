package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Long> {
}
