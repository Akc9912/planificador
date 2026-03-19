package aktech.planificador.modules.subject.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import aktech.planificador.modules.subject.domain.model.Subject;
import aktech.planificador.modules.subject.domain.model.SubjectSchedule;
import aktech.planificador.modules.subject.dto.SubjectScheduleCreateRequestDto;
import aktech.planificador.modules.subject.dto.SubjectScheduleResponseDto;
import aktech.planificador.modules.subject.dto.SubjectScheduleUpdateRequestDto;
import aktech.planificador.modules.subject.persistence.SubjectScheduleRepository;
import aktech.planificador.shared.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class SubjectScheduleServiceTest {

    @Mock
    private SubjectScheduleRepository subjectScheduleRepository;

    @Mock
    private SubjectCareerAccessService subjectCareerAccessService;

    private SubjectScheduleService subjectScheduleService;

    @BeforeEach
    void setUp() {
        subjectScheduleService = new SubjectScheduleService(subjectScheduleRepository, subjectCareerAccessService);
    }

    @Test
    void createSchedule_shouldNormalizeDayAndPersist() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();

        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId))
                .thenReturn(createOwnedSubject(subjectId));

        SubjectScheduleCreateRequestDto request = new SubjectScheduleCreateRequestDto();
        request.setDayOfWeek("lunes");
        request.setStartTime(LocalTime.of(9, 0));
        request.setEndTime(LocalTime.of(11, 0));

        when(subjectScheduleRepository.save(any(SubjectSchedule.class))).thenAnswer(invocation -> {
            SubjectSchedule schedule = invocation.getArgument(0);
            schedule.setId(scheduleId);
            return schedule;
        });

        SubjectScheduleResponseDto response = subjectScheduleService.createSchedule(userId, subjectId, request);

        ArgumentCaptor<SubjectSchedule> captor = ArgumentCaptor.forClass(SubjectSchedule.class);
        verify(subjectScheduleRepository).save(captor.capture());

        SubjectSchedule saved = captor.getValue();
        assertEquals("LUNES", saved.getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), saved.getStartTime());
        assertEquals(LocalTime.of(11, 0), saved.getEndTime());

        assertEquals(scheduleId, response.getId());
        assertEquals("LUNES", response.getDayOfWeek());
    }

    @Test
    void createSchedule_shouldThrowWhenTimeRangeIsInvalid() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId))
                .thenReturn(createOwnedSubject(subjectId));

        SubjectScheduleCreateRequestDto request = new SubjectScheduleCreateRequestDto();
        request.setDayOfWeek("MARTES");
        request.setStartTime(LocalTime.of(14, 0));
        request.setEndTime(LocalTime.of(13, 0));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> subjectScheduleService.createSchedule(userId, subjectId, request));

        assertEquals("La hora de fin debe ser mayor a la hora de inicio", ex.getMessage());
    }

    @Test
    void updateSchedule_shouldThrowWhenScheduleDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();

        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId))
                .thenReturn(createOwnedSubject(subjectId));
        when(subjectScheduleRepository.findByIdAndSubjectId(scheduleId, subjectId)).thenReturn(Optional.empty());

        SubjectScheduleUpdateRequestDto request = new SubjectScheduleUpdateRequestDto();
        request.setDayOfWeek("VIERNES");

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> subjectScheduleService.updateSchedule(userId, subjectId, scheduleId, request));

        assertEquals("Horario de materia no encontrado", ex.getMessage());
    }

    @Test
    void deleteSchedule_shouldDeleteWhenExists() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();

        when(subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId))
                .thenReturn(createOwnedSubject(subjectId));

        SubjectSchedule schedule = new SubjectSchedule();
        schedule.setId(scheduleId);
        schedule.setSubjectId(subjectId);

        when(subjectScheduleRepository.findByIdAndSubjectId(scheduleId, subjectId)).thenReturn(Optional.of(schedule));

        subjectScheduleService.deleteSchedule(userId, subjectId, scheduleId);

        verify(subjectScheduleRepository).delete(schedule);
    }

    private Subject createOwnedSubject(UUID subjectId) {
        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setCareerId(UUID.randomUUID());
        return subject;
    }
}
