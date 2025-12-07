package org.cswteams.ms3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for enabling Spring AOP and AspectJ auto-proxying.
 *
 * <p>This class serves as a central configuration component, enabling the use of
 * Aspect-Oriented Programming (AOP) within the Spring application. By annotating
 * the class with {@link Configuration}, it is recognized as a source of bean
 * definitions and application context configuration.</p>
 *
 * <p>The {@link EnableAspectJAutoProxy} annotation enables automatic proxy creation
 * for beans annotated with AspectJ annotations, such as {@code @Aspect}. This allows
 * for cross-cutting concerns, such as logging, transactions, or security, to be
 * implemented declaratively.</p>
 *
 * <p><b>Usage:</b></p>
 * <ul>
 *   <li>Define your aspect classes using the {@code @Aspect} annotation.</li>
 *   <li>Ensure this configuration class is scanned or explicitly imported into
 *       your application context.</li>
 * </ul>
 *
 * <pre>
 * &#64;Aspect
 * &#64;Component
 * public class LoggingAspect {
 *     &#64;Before("execution(* com.example.service.*.*(..))")
 *     public void logBefore(JoinPoint joinPoint) {
 *         System.out.println("Method called: " + joinPoint.getSignature());
 *     }
 * }
 * </pre>
 *
 * <p>To include this configuration, ensure it is picked up by Spring's component
 * scanning or explicitly imported:</p>
 *
 * <pre>
 * &#64;SpringBootApplication
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * </pre>
 *
 * @see Configuration
 * @see EnableAspectJAutoProxy
 */
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
