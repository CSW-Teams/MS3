package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedTokenDAO
        extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByToken(String token);
}
