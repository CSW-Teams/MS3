package org.cswteams.ms3.config;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.Filter;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class SoftDeleteFilterWebFilter implements Filter {

    @Autowired
    private SoftDeleteService softDeleteService;

    @Override
    @Transactional
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        // Assicurati che il filtro venga abilitato in un contesto transazionale
        softDeleteService.enableSoftDeleteFilter("softDeleteService", Map.of());

        // proseguo con la request
        chain.doFilter(request, response);
    }
}
