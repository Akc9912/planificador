package aktech.planificador.modules.auth.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import aktech.planificador.modules.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthJwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    private AuthJwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new AuthJwtAuthenticationFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_shouldContinueWhenAuthorizationHeaderMissing() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/careers");
        MockHttpServletResponse response = new MockHttpServletResponse();
        TrackingFilterChain chain = new TrackingFilterChain();

        filter.doFilter(request, response, chain);

        assertTrue(chain.wasCalled);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_shouldReturnUnauthorizedWhenTokenInvalid() throws ServletException, IOException {
        String token = "invalid-token";
        MockHttpServletRequest request = requestWithBearer(token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        TrackingFilterChain chain = new TrackingFilterChain();

        when(jwtService.isTokenValid(token)).thenReturn(false);

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(!chain.wasCalled);
    }

    @Test
    void doFilter_shouldAuthenticateAndContinueWhenTokenValid() throws ServletException, IOException {
        String token = "valid-token";
        UUID userId = UUID.randomUUID();

        MockHttpServletRequest request = requestWithBearer(token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        TrackingFilterChain chain = new TrackingFilterChain();

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUserId(token)).thenReturn(userId);
        when(jwtService.extractRole(token)).thenReturn("service_role");

        filter.doFilter(request, response, chain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userId.toString(), authentication.getPrincipal());
        assertEquals("ROLE_ADMIN", authentication.getAuthorities().iterator().next().getAuthority());
        assertTrue(chain.wasCalled);
    }

    @Test
    void doFilter_shouldReturnUnauthorizedWhenUserIdMissing() throws ServletException, IOException {
        String token = "token-without-user-id";

        MockHttpServletRequest request = requestWithBearer(token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        TrackingFilterChain chain = new TrackingFilterChain();

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUserId(token)).thenReturn(null);

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(!chain.wasCalled);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_shouldClearContextAndReturnUnauthorizedOnException() throws ServletException, IOException {
        String token = "token-that-throws";

        MockHttpServletRequest request = requestWithBearer(token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        TrackingFilterChain chain = new TrackingFilterChain();

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("existing-user", null));

        when(jwtService.isTokenValid(token)).thenThrow(new RuntimeException("broken parser"));

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(!chain.wasCalled);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private MockHttpServletRequest requestWithBearer(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/careers");
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }

    private static class TrackingFilterChain implements FilterChain {
        private boolean wasCalled;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response)
                throws IOException, ServletException {
            wasCalled = true;
        }
    }
}
