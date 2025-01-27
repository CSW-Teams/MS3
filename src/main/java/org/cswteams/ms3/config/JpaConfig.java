package org.cswteams.ms3.config;

import org.cswteams.ms3.dao.soft_delete.SoftDeleteJpaRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "org.cswteams.ms3.dao", repositoryBaseClass = SoftDeleteJpaRepositoryImpl.class)
public class JpaConfig {
}