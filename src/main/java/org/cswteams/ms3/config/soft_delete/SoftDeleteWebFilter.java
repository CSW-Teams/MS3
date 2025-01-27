package org.cswteams.ms3.config.soft_delete;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.*;
import java.io.IOException;
import java.util.Map;

/**
 * A web filter for enabling Hibernates soft delete filter.
 * This filter is applied with the lowest precedence to ensure it is executed
 * after other filters in the chain.
 *
 * <p>The filter uses {@link SoftDeleteService} to enable a Hibernate filter
 * dynamically during the processing of a request.</p>
 *
 * <p>Annotations used:</p>
 * <ul>
 *     <li>{@code @Component} - Marks the filter as a Spring-managed component.</li>
 *     <li>{@code @Order(Ordered.LOWEST_PRECEDENCE)} - Ensures this filter executes last.</li>
 * </ul>
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class SoftDeleteWebFilter implements Filter {

    /** Logger instance for debugging and logging filter activities. */
    Logger logger = LoggerFactory.getLogger(SoftDeleteWebFilter.class);

    /** Service used to enable the Hibernate soft delete filter. */
    @Autowired
    private SoftDeleteService softDeleteService;

    /**
     * Initializes the filter. Logs a debug message indicating that the filter has been initialized.
     *
     * @param filterConfig the filter configuration object
     * @throws ServletException if an error occurs during filter initialization
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("Initialized SoftDeleteWebFilter");
    }

    /**
     * Enables the Hibernate soft delete filter and processes the request.
     *
     * <p>The method performs the following steps:</p>
     * <ol>
     *     <li>Logs a message indicating the start of the filter activation.</li>
     *     <li>Enables the Hibernate soft delete filter using {@link SoftDeleteService}.</li>
     *     <li>Logs a message indicating the successful activation of the filter.</li>
     *     <li>Proceeds with the filter chain execution.</li>
     * </ol>
     *
     * @param request  the incoming {@link ServletRequest}
     * @param response the outgoing {@link ServletResponse}
     * @param chain    the {@link FilterChain} for processing the request and response
     * @throws IOException      if an I/O error occurs during processing
     * @throws ServletException if a servlet error occurs during processing
     */
    @Override
    @Transactional
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {
        logger.debug("Enabling hibernate SoftDeleteFilter");

        softDeleteService.enableSoftDeleteFilter("softDeleteService", Map.of());

        logger.debug("Enabled hibernate SoftDeleteFilter");

        chain.doFilter(request, response);
    }
}