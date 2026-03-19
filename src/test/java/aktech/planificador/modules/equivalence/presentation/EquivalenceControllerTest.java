package aktech.planificador.modules.equivalence.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import aktech.planificador.modules.equivalence.application.EquivalenceService;
import aktech.planificador.modules.equivalence.dto.EquivalenceCreateRequestDto;
import aktech.planificador.modules.equivalence.dto.EquivalenceResponseDto;
import aktech.planificador.modules.equivalence.dto.EquivalenceUpdateRequestDto;
import aktech.planificador.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class EquivalenceControllerTest {

    @Mock
    private EquivalenceService equivalenceService;

    private EquivalenceController equivalenceController;

    @BeforeEach
    void setUp() {
        equivalenceController = new EquivalenceController(equivalenceService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void create_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        EquivalenceCreateRequestDto request = new EquivalenceCreateRequestDto();
        EquivalenceResponseDto expected = new EquivalenceResponseDto();

        when(equivalenceService.createEquivalence(userId, request)).thenReturn(expected);

        EquivalenceResponseDto response = equivalenceController.create(request);

        assertSame(expected, response);
        verify(equivalenceService).createEquivalence(userId, request);
    }

    @Test
    void listByUser_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        List<EquivalenceResponseDto> expected = List.of(new EquivalenceResponseDto());
        when(equivalenceService.listByUser(userId)).thenReturn(expected);

        List<EquivalenceResponseDto> response = equivalenceController.listByUser();

        assertSame(expected, response);
        verify(equivalenceService).listByUser(userId);
    }

    @Test
    void listBySubject_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        List<EquivalenceResponseDto> expected = List.of(new EquivalenceResponseDto());
        when(equivalenceService.listBySubject(userId, subjectId)).thenReturn(expected);

        List<EquivalenceResponseDto> response = equivalenceController.listBySubject(subjectId);

        assertSame(expected, response);
        verify(equivalenceService).listBySubject(userId, subjectId);
    }

    @Test
    void getOwned_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID equivalenceId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        EquivalenceResponseDto expected = new EquivalenceResponseDto();
        when(equivalenceService.getOwnedOrThrow(equivalenceId, userId)).thenReturn(expected);

        EquivalenceResponseDto response = equivalenceController.getOwned(equivalenceId);

        assertSame(expected, response);
        verify(equivalenceService).getOwnedOrThrow(equivalenceId, userId);
    }

    @Test
    void update_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID equivalenceId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        EquivalenceUpdateRequestDto request = new EquivalenceUpdateRequestDto();
        EquivalenceResponseDto expected = new EquivalenceResponseDto();

        when(equivalenceService.updateEquivalence(equivalenceId, userId, request)).thenReturn(expected);

        EquivalenceResponseDto response = equivalenceController.update(equivalenceId, request);

        assertSame(expected, response);
        verify(equivalenceService).updateEquivalence(equivalenceId, userId, request);
    }

    @Test
    void delete_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID equivalenceId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        equivalenceController.delete(equivalenceId);

        verify(equivalenceService).deleteEquivalence(equivalenceId, userId);
    }

    @Test
    void listByUser_shouldThrowWhenNoAuthenticatedUser() {
        SecurityContextHolder.clearContext();

        BusinessException ex = assertThrows(BusinessException.class, () -> equivalenceController.listByUser());

        assertEquals("No hay usuario autenticado", ex.getMessage());
    }

    @Test
    void listByUser_shouldThrowWhenTokenUserIdIsNotUuid() {
        setAuthenticatedUser("not-a-uuid");

        BusinessException ex = assertThrows(BusinessException.class, () -> equivalenceController.listByUser());

        assertEquals("Token invalido: userId no es UUID", ex.getMessage());
    }

    private void setAuthenticatedUser(String principal) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
