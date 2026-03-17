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

import aktech.planificador.modules.subject.dto.SubjectModuleCreateRequestDto;
import aktech.planificador.modules.subject.dto.SubjectModuleResponseDto;
import aktech.planificador.modules.subject.dto.SubjectModuleUpdateRequestDto;
import aktech.planificador.modules.subject.service.SubjectModuleService;
import aktech.planificador.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class SubjectModuleControllerTest {

    @Mock
    private SubjectModuleService subjectModuleService;

    private SubjectModuleController subjectModuleController;

    @BeforeEach
    void setUp() {
        subjectModuleController = new SubjectModuleController(subjectModuleService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void listBySubject_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        List<SubjectModuleResponseDto> expected = List.of(new SubjectModuleResponseDto());
        when(subjectModuleService.listBySubject(userId, subjectId)).thenReturn(expected);

        List<SubjectModuleResponseDto> response = subjectModuleController.listBySubject(subjectId);

        assertSame(expected, response);
        verify(subjectModuleService).listBySubject(userId, subjectId);
    }

    @Test
    void createModule_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        SubjectModuleCreateRequestDto request = new SubjectModuleCreateRequestDto();
        SubjectModuleResponseDto expected = new SubjectModuleResponseDto();

        when(subjectModuleService.createModule(userId, subjectId, request)).thenReturn(expected);

        SubjectModuleResponseDto response = subjectModuleController.createModule(subjectId, request);

        assertSame(expected, response);
        verify(subjectModuleService).createModule(userId, subjectId, request);
    }

    @Test
    void updateModule_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID moduleId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        SubjectModuleUpdateRequestDto request = new SubjectModuleUpdateRequestDto();
        SubjectModuleResponseDto expected = new SubjectModuleResponseDto();

        when(subjectModuleService.updateModule(userId, subjectId, moduleId, request)).thenReturn(expected);

        SubjectModuleResponseDto response = subjectModuleController.updateModule(subjectId, moduleId, request);

        assertSame(expected, response);
        verify(subjectModuleService).updateModule(userId, subjectId, moduleId, request);
    }

    @Test
    void deleteModule_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID moduleId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        subjectModuleController.deleteModule(subjectId, moduleId);

        verify(subjectModuleService).deleteModule(userId, subjectId, moduleId);
    }

    @Test
    void listBySubject_shouldThrowWhenTokenUserIdIsInvalid() {
        setAuthenticatedUser("not-a-uuid");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> subjectModuleController.listBySubject(UUID.randomUUID()));

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
