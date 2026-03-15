package aktech.planificador.modules.auth.filter;

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

import aktech.planificador.modules.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthJwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthJwtAuthenticationFilter.class);

    private final JwtService jwtService;

    public AuthJwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            if (!jwtService.isTokenValid(jwt)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UUID userId = jwtService.extractUserId(jwt);
                if (userId == null) {
                    logger.warn("Token sin userId UUID valido. {} {}", request.getMethod(), request.getRequestURI());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                String normalizedRole = normalizeRole(jwtService.extractRole(jwt));
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId.toString(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + normalizedRole)));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception ex) {
            logger.warn("Error validando JWT: {}", ex.getMessage());
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
