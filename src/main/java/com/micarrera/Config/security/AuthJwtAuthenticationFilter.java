package com.micarrera.Config.security;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.micarrera.modules.auth.api.JwtApi;
import lombok.RequiredArgsConstructor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AuthJwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthJwtAuthenticationFilter.class);

    private final JwtApi jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        logger.info("[JWT-DEBUG] {} {} - Authorization header: {}", request.getMethod(), request.getRequestURI(),
                authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("[JWT-DEBUG] Falta header Authorization o formato incorrecto. {} {}", request.getMethod(),
                    request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        logger.info("[JWT-DEBUG] Token extraído: {}", jwt);

        try {
            if (!jwtService.isTokenValid(jwt)) {
                logger.warn("[JWT-DEBUG] Token inválido: {}", jwt);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UUID userId = jwtService.extractUserId(jwt);
                logger.info("[JWT-DEBUG] userId extraído: {}", userId);
                if (userId == null) {
                    logger.warn("[JWT-DEBUG] Token sin userId UUID válido. {} {}", request.getMethod(),
                            request.getRequestURI());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                String normalizedRole = normalizeRole(jwtService.extractRole(jwt));
                logger.info("[JWT-DEBUG] Rol extraído: {}", normalizedRole);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId.toString(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + normalizedRole)));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception ex) {
            logger.warn("[JWT-DEBUG] Error validando JWT: {}", ex.getMessage(), ex);
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String normalizeRole(String rawRole) {
        if (rawRole == null || rawRole.isBlank()) {
            return "USER";
        }

        String roleUpper = rawRole.trim().toUpperCase(Locale.ROOT);
        if ("AUTHENTICATED".equals(roleUpper) || "USER".equals(roleUpper)) {
            return "USER";
        }
        if ("ADMIN".equals(roleUpper) || "SERVICE_ROLE".equals(roleUpper)) {
            return "ADMIN";
        }
        return roleUpper;
    }
}
