package org.cswteams.ms3.config.soft_delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.*;
import java.io.IOException;
import java.util.Map;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class SoftDeleteWebFilter implements Filter {

    @Autowired
    private SoftDeleteService softDeleteService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

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