package aktech.planificador.modules.auth.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import aktech.planificador.Config.SecurityConfig;
import aktech.planificador.modules.auth.controller.AuthModuleController;
import aktech.planificador.modules.auth.dto.LoginResponseDto;
import aktech.planificador.modules.auth.filter.AuthJwtAuthenticationFilter;
import aktech.planificador.modules.auth.service.AuthSessionService;
import aktech.planificador.modules.auth.service.JwtService;

@WebMvcTest(controllers = AuthModuleController.class)
@Import({ SecurityConfig.class, AuthJwtAuthenticationFilter.class })
class SecurityConfigAuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthSessionService authSessionService;

    @MockBean
    private JwtService jwtService;

    @Test
    void loginEndpoint_shouldBePublic() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.operation").value("login"));
    }

    @Test
    void registerAdmin_shouldBeForbiddenForAnonymous() throws Exception {
        mockMvc.perform(post("/auth/register-admin")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void registerAdmin_shouldBeForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(post("/auth/register-admin")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerAdmin_shouldBeAllowedForAdmin() throws Exception {
        mockMvc.perform(post("/auth/register-admin")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.operation").value("register-admin"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void legacyAuthPath_shouldAlwaysBeForbidden() throws Exception {
        mockMvc.perform(get("/legacy/auth/login"))
                .andExpect(status().isForbidden());
    }

    @Test
    void me_shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void me_shouldWorkForAuthenticatedUser() throws Exception {
        LoginResponseDto session = new LoginResponseDto();
        session.setAccessToken("token");
        session.setUserId("123e4567-e89b-12d3-a456-426614174000");
        session.setEmail("user@example.com");
        session.setRole("USER");

        when(authSessionService.getSessionFromAuthorizationHeader(any())).thenReturn(session);

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(authSessionService).getSessionFromAuthorizationHeader(any());
    }
}
