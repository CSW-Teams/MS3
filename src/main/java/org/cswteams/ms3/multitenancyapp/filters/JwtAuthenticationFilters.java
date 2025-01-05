package org.cswteams.ms3.multitenancyapp.filters;

import org.cswteams.ms3.multitenancyapp.dto.login.CustomUserDetails;
import org.cswteams.ms3.multitenancyapp.control.login.LoginController;
import org.cswteams.ms3.multitenancyapp.tenant.TenantContext;
import org.cswteams.ms3.multitenancyapp.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilters extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilters.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private LoginController loginController;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username;
        String jwt;

        logger.info("Authorization header: {}", authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtTokenUtil.extractUsername(jwt);
        } else {
            // Log missing or invalid token header and allow unauthenticated endpoints to bypass
            logger.info("Missing or invalid Authorization header for request: {}", request.getRequestURI());

            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomUserDetails loggedUserDTO;

            try {
                loggedUserDTO = (CustomUserDetails) this.loginController.loadUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("SystemUser not found");
                return;
            }

            if (jwtTokenUtil.validateToken(jwt, loggedUserDTO)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(loggedUserDTO, null, loggedUserDTO.getAuthorities());

                logger.info("UsernamePasswordAuthToken: {}", usernamePasswordAuthenticationToken);

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                String tenantId = jwtTokenUtil.parseTenantFromJwt(jwt);
                TenantContext.setCurrentTenant(tenantId);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}