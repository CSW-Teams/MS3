package org.cswteams.ms3.filters;

import org.cswteams.ms3.control.login.LoginController;
import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.cswteams.ms3.tenant.TenantContext;
import org.cswteams.ms3.utils.JwtUtil;
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
public class JwtRequestFilters extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilters.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LoginController loginController;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username;
        String jwt;

        logger.debug("Authorization header: {}", authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        } else {
            // Log missing or invalid token header and allow unauthenticated endpoints to bypass
            logger.debug("Missing or invalid Authorization header for request: {}", request.getRequestURI());

            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomUserDetails loggedUserDTO;

            try {
                loggedUserDTO = (CustomUserDetails) this.loginController.loadUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("TenantUser not found");
                return;
            }

            if (jwtUtil.validateToken(jwt, loggedUserDTO)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(loggedUserDTO, null, loggedUserDTO.getAuthorities());

                logger.debug("UsernamePasswordAuthToken: {}", usernamePasswordAuthenticationToken);

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                String tenantId = jwtUtil.parseTenantFromJwt(jwt);
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
