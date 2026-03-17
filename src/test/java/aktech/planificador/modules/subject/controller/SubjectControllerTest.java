package aktech.planificador.modules.subject.controller;

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

import aktech.planificador.modules.subject.dto.SubjectCreateRequestDto;
import aktech.planificador.modules.subject.dto.SubjectResponseDto;
import aktech.planificador.modules.subject.dto.SubjectUpdateRequestDto;
import aktech.planificador.modules.subject.service.SubjectService;
import aktech.planificador.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class SubjectControllerTest {

    @Mock
    private SubjectService subjectService;

    private SubjectController subjectController;

    @BeforeEach
    void setUp() {
        subjectController = new SubjectController(subjectService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createSubject_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        SubjectCreateRequestDto request = new SubjectCreateRequestDto();
        SubjectResponseDto expected = new SubjectResponseDto();

        when(subjectService.createSubject(userId, request)).thenReturn(expected);

        SubjectResponseDto response = subjectController.createSubject(request);

        assertSame(expected, response);
        verify(subjectService).createSubject(userId, request);
    }

    @Test
    void listByCareer_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        List<SubjectResponseDto> expected = List.of(new SubjectResponseDto());
        when(subjectService.listByCareer(userId, careerId)).thenReturn(expected);

        List<SubjectResponseDto> response = subjectController.listByCareer(careerId);

        assertSame(expected, response);
        verify(subjectService).listByCareer(userId, careerId);
    }

    @Test
    void listByCareerAndStatus_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID careerId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        List<SubjectResponseDto> expected = List.of(new SubjectResponseDto());
        when(subjectService.listByCareerAndStatus(userId, careerId, "cursando")).thenReturn(expected);

        List<SubjectResponseDto> response = subjectController.listByCareerAndStatus(careerId, "cursando");

        assertSame(expected, response);
        verify(subjectService).listByCareerAndStatus(userId, careerId, "cursando");
    }

    @Test
    void getByIdOwned_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        SubjectResponseDto expected = new SubjectResponseDto();
        when(subjectService.getOwnedOrThrow(subjectId, userId)).thenReturn(expected);

        SubjectResponseDto response = subjectController.getByIdOwned(subjectId);

        assertSame(expected, response);
        verify(subjectService).getOwnedOrThrow(subjectId, userId);
    }

    @Test
    void updateSubject_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        SubjectUpdateRequestDto request = new SubjectUpdateRequestDto();
        SubjectResponseDto expected = new SubjectResponseDto();

        when(subjectService.updateSubject(subjectId, userId, request)).thenReturn(expected);

        SubjectResponseDto response = subjectController.updateSubject(subjectId, request);

        assertSame(expected, response);
        verify(subjectService).updateSubject(subjectId, userId, request);
    }

    @Test
    void deleteSubject_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        subjectController.deleteSubject(subjectId);

        verify(subjectService).deleteSubject(subjectId, userId);
    }

    @Test
    void ownsSubject_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        when(subjectService.ownsSubject(subjectId, userId)).thenReturn(true);

        boolean response = subjectController.ownsSubject(subjectId);

        assertEquals(true, response);
        verify(subjectService).ownsSubject(subjectId, userId);
    }

    @Test
    void listByCareer_shouldThrowWhenNoAuthenticatedUser() {
        SecurityContextHolder.clearContext();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> subjectController.listByCareer(UUID.randomUUID()));

        assertEquals("No hay usuario autenticado", ex.getMessage());
    }

    @Test
    void ownsSubject_shouldThrowWhenTokenUserIdIsNotUuid() {
        setAuthenticatedUser("not-a-uuid");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> subjectController.ownsSubject(UUID.randomUUID()));

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
