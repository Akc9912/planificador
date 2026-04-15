package com.micarrera.modules.subject.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.micarrera.modules.subject.service.SubjectScheduleServiceImpl;
import com.micarrera.modules.subject.api.dto.SubjectScheduleCreateRequestDto;
import com.micarrera.modules.subject.api.dto.SubjectScheduleResponseDto;
import com.micarrera.modules.subject.api.dto.SubjectScheduleUpdateRequestDto;
import com.micarrera.shared.exception.BusinessException;

@RestController
@lombok.RequiredArgsConstructor
@RequestMapping("/subjects/{subjectId}/schedules")
public class SubjectScheduleController {

    private final SubjectScheduleServiceImpl subjectScheduleService;


    @GetMapping
    public List<SubjectScheduleResponseDto> listBySubject(@PathVariable UUID subjectId) {
        UUID userId = getAuthenticatedUserId();
        return subjectScheduleService.listBySubject(userId, subjectId);
    }

    @PostMapping
    public SubjectScheduleResponseDto createSchedule(
            @PathVariable UUID subjectId,
            @RequestBody SubjectScheduleCreateRequestDto request) {
        UUID userId = getAuthenticatedUserId();
        return subjectScheduleService.createSchedule(userId, subjectId, request);
    }

    @PutMapping("/{scheduleId}")
    public SubjectScheduleResponseDto updateSchedule(
            @PathVariable UUID subjectId,
            @PathVariable UUID scheduleId,
            @RequestBody SubjectScheduleUpdateRequestDto request) {
        UUID userId = getAuthenticatedUserId();
        return subjectScheduleService.updateSchedule(userId, subjectId, scheduleId, request);
    }

    @DeleteMapping("/{scheduleId}")
    public void deleteSchedule(
            @PathVariable UUID subjectId,
            @PathVariable UUID scheduleId) {
        UUID userId = getAuthenticatedUserId();
        subjectScheduleService.deleteSchedule(userId, subjectId, scheduleId);
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("No hay usuario autenticado");
        }

        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Token invalido: userId no es UUID");
        }
    }
}
