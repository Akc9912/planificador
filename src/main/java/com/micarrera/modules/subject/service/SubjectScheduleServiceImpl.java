package com.micarrera.modules.subject.service;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.micarrera.modules.subject.entity.SubjectSchedule;
import com.micarrera.modules.subject.api.dto.SubjectScheduleCreateRequestDto;
import com.micarrera.modules.subject.api.dto.SubjectScheduleResponseDto;
import com.micarrera.modules.subject.api.dto.SubjectScheduleUpdateRequestDto;
import com.micarrera.modules.subject.enums.SubjectWeekDay;
import com.micarrera.modules.subject.repository.SubjectScheduleRepository;
import com.micarrera.shared.exception.NotFoundException;
import com.micarrera.shared.util.ValidationUtils;

@Service
@lombok.RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectScheduleServiceImpl {

    private final SubjectScheduleRepository subjectScheduleRepository;
    private final SubjectCareerAccessServiceImpl subjectCareerAccessService;


    public List<SubjectScheduleResponseDto> listBySubject(UUID userId, UUID subjectId) {
        requireUserId(userId);
        requireSubjectId(subjectId);
        subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId);

        return subjectScheduleRepository.findBySubjectIdOrderByDayOfWeekAscStartTimeAsc(subjectId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional
    public SubjectScheduleResponseDto createSchedule(
            UUID userId,
            UUID subjectId,
            SubjectScheduleCreateRequestDto request) {
        requireUserId(userId);
        requireSubjectId(subjectId);
        ValidationUtils.requireNotNull(request, "El body de creacion es obligatorio");

        subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId);

        SubjectSchedule schedule = new SubjectSchedule();
        schedule.setSubjectId(subjectId);
        schedule.setDayOfWeek(SubjectWeekDay.normalize(request.getDayOfWeek()));
        schedule.setStartTime(requireTime(request.getStartTime(), "hora de inicio"));
        schedule.setEndTime(requireTime(request.getEndTime(), "hora de fin"));
        validateTimeRange(schedule.getStartTime(), schedule.getEndTime());

        return toResponseDto(subjectScheduleRepository.save(schedule));
    }

    @Transactional
    public SubjectScheduleResponseDto updateSchedule(
            UUID userId,
            UUID subjectId,
            UUID scheduleId,
            SubjectScheduleUpdateRequestDto request) {
        requireUserId(userId);
        requireSubjectId(subjectId);
        requireScheduleId(scheduleId);
        ValidationUtils.requireNotNull(request, "El body de actualizacion es obligatorio");

        subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId);

        SubjectSchedule schedule = subjectScheduleRepository.findByIdAndSubjectId(scheduleId, subjectId)
                .orElseThrow(() -> new NotFoundException("Horario de materia no encontrado"));

        if (request.getDayOfWeek() != null) {
            schedule.setDayOfWeek(SubjectWeekDay.normalize(request.getDayOfWeek()));
        }

        if (request.getStartTime() != null) {
            schedule.setStartTime(requireTime(request.getStartTime(), "hora de inicio"));
        }

        if (request.getEndTime() != null) {
            schedule.setEndTime(requireTime(request.getEndTime(), "hora de fin"));
        }

        validateTimeRange(schedule.getStartTime(), schedule.getEndTime());

        return toResponseDto(subjectScheduleRepository.save(schedule));
    }

    @SuppressWarnings("null")
    @Transactional
    public void deleteSchedule(UUID userId, UUID subjectId, UUID scheduleId) {
        requireUserId(userId);
        requireSubjectId(subjectId);
        requireScheduleId(scheduleId);

        subjectCareerAccessService.getOwnedSubjectOrThrow(userId, subjectId);

        SubjectSchedule schedule = subjectScheduleRepository.findByIdAndSubjectId(scheduleId, subjectId)
                .orElseThrow(() -> new NotFoundException("Horario de materia no encontrado"));

        subjectScheduleRepository.delete(schedule);
    }

    private LocalTime requireTime(LocalTime value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("El campo " + fieldName + " es obligatorio");
        }
        return value;
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("La hora de fin debe ser mayor a la hora de inicio");
        }
    }

    private SubjectScheduleResponseDto toResponseDto(SubjectSchedule schedule) {
        SubjectScheduleResponseDto dto = new SubjectScheduleResponseDto();
        dto.setId(schedule.getId());
        dto.setSubjectId(schedule.getSubjectId());
        dto.setDayOfWeek(schedule.getDayOfWeek());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setCreatedAt(schedule.getCreatedAt());
        dto.setUpdatedAt(schedule.getUpdatedAt());
        return dto;
    }

    private void requireUserId(UUID userId) {
        ValidationUtils.requireNotNull(userId, "El id de usuario es obligatorio");
    }

    private void requireSubjectId(UUID subjectId) {
        ValidationUtils.requireNotNull(subjectId, "El id de materia es obligatorio");
    }

    private void requireScheduleId(UUID scheduleId) {
        ValidationUtils.requireNotNull(scheduleId, "El id de horario es obligatorio");
    }
}
