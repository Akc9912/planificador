package aktech.planificador.modules.subject.presentation;

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

import aktech.planificador.modules.subject.application.SubjectScheduleService;
import aktech.planificador.modules.subject.dto.SubjectScheduleCreateRequestDto;
import aktech.planificador.modules.subject.dto.SubjectScheduleResponseDto;
import aktech.planificador.modules.subject.dto.SubjectScheduleUpdateRequestDto;
import aktech.planificador.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class SubjectScheduleControllerTest {

    @Mock
    private SubjectScheduleService subjectScheduleService;

    private SubjectScheduleController subjectScheduleController;

    @BeforeEach
    void setUp() {
        subjectScheduleController = new SubjectScheduleController(subjectScheduleService);
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

        List<SubjectScheduleResponseDto> expected = List.of(new SubjectScheduleResponseDto());
        when(subjectScheduleService.listBySubject(userId, subjectId)).thenReturn(expected);

        List<SubjectScheduleResponseDto> response = subjectScheduleController.listBySubject(subjectId);

        assertSame(expected, response);
        verify(subjectScheduleService).listBySubject(userId, subjectId);
    }

    @Test
    void createSchedule_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        SubjectScheduleCreateRequestDto request = new SubjectScheduleCreateRequestDto();
        SubjectScheduleResponseDto expected = new SubjectScheduleResponseDto();

        when(subjectScheduleService.createSchedule(userId, subjectId, request)).thenReturn(expected);

        SubjectScheduleResponseDto response = subjectScheduleController.createSchedule(subjectId, request);

        assertSame(expected, response);
        verify(subjectScheduleService).createSchedule(userId, subjectId, request);
    }

    @Test
    void updateSchedule_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        SubjectScheduleUpdateRequestDto request = new SubjectScheduleUpdateRequestDto();
        SubjectScheduleResponseDto expected = new SubjectScheduleResponseDto();

        when(subjectScheduleService.updateSchedule(userId, subjectId, scheduleId, request)).thenReturn(expected);

        SubjectScheduleResponseDto response = subjectScheduleController.updateSchedule(subjectId, scheduleId, request);

        assertSame(expected, response);
        verify(subjectScheduleService).updateSchedule(userId, subjectId, scheduleId, request);
    }

    @Test
    void deleteSchedule_shouldDelegateUsingAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        setAuthenticatedUser(userId.toString());

        subjectScheduleController.deleteSchedule(subjectId, scheduleId);

        verify(subjectScheduleService).deleteSchedule(userId, subjectId, scheduleId);
    }

    @Test
    void createSchedule_shouldThrowWhenNoAuthenticatedUser() {
        SecurityContextHolder.clearContext();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> subjectScheduleController.createSchedule(UUID.randomUUID(),
                        new SubjectScheduleCreateRequestDto()));

        assertEquals("No hay usuario autenticado", ex.getMessage());
    }

    private void setAuthenticatedUser(String principal) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
