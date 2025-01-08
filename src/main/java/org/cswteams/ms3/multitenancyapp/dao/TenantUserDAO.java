package org.cswteams.ms3.multitenancyapp.dao;

import org.cswteams.ms3.multitenancyapp.entity.TenantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface TenantUserDAO extends JpaRepository<TenantUser, Long> {
    TenantUser findByEmail(String email);

    Optional<TenantUser> findByTaxCode(@NotNull String taxCode);

}
