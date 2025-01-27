package org.cswteams.ms3.config;

import org.cswteams.ms3.dao.soft_delete.SoftDeleteJpaRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration class for setting up JPA repositories with custom repository base implementation.
 *
 * <p>This class configures the JPA repository layer of the application by enabling
 * the scanning of repository interfaces and associating them with a custom base
 * repository implementation.</p>
 *
 * <p>The {@link EnableJpaRepositories} annotation is used to define the base package
 * where repository interfaces are located and to specify a custom base repository
 * implementation, {@link SoftDeleteJpaRepositoryImpl}, for handling soft delete operations.</p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Scans the package <code>org.cswteams.ms3.dao</code> for JPA repository interfaces.</li>
 *   <li>Uses {@link SoftDeleteJpaRepositoryImpl} as the base class for all repositories,
 *       enabling custom logic such as soft delete functionality.</li>
 * </ul>
 *
 * <p><b>Usage:</b></p>
 * <ul>
 *   <li>Define repository interfaces in the package <code>org.cswteams.ms3.dao</code>.</li>
 *   <li>Extend the {@code SoftDeleteJpaRepository} interface in your custom repositories
 *       to inherit soft delete functionality.</li>
 * </ul>
 *
 * <p><b>Example:</b></p>
 * <pre>
 * &#64;NoRepositoryBean
 * public interface SoftDeleteJpaRepository<T, ID> extends JpaRepository&lt;T, ID&gt; {
 *     void restoreById(ID id);
 * }
 *
 * &#64;Repository
 * public interface MyEntityRepository extends SoftDeleteJpaRepository&lt;MyEntity, Long&gt; {
 * }
 * </pre>
 *
 * @see Configuration
 * @see EnableJpaRepositories
 * @see SoftDeleteJpaRepositoryImpl
 */
@Configuration
@EnableJpaRepositories(basePackages = "org.cswteams.ms3.dao", repositoryBaseClass = SoftDeleteJpaRepositoryImpl.class)
public class JpaConfig {
}