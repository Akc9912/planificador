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

import com.micarrera.modules.subject.api.dto.SubjectModuleCreateRequestDto;
import com.micarrera.modules.subject.api.dto.SubjectModuleResponseDto;
import com.micarrera.modules.subject.api.dto.SubjectModuleUpdateRequestDto;
import com.micarrera.modules.subject.service.SubjectModuleServiceImpl;
import com.micarrera.shared.exception.BusinessException;

@RestController
@RequestMapping("/subjects/{subjectId}/modules")
public class SubjectModuleController {

    private final SubjectModuleServiceImpl subjectModuleService;

    public SubjectModuleController(SubjectModuleServiceImpl subjectModuleService) {
        this.subjectModuleService = subjectModuleService;
    }

    @GetMapping
    public List<SubjectModuleResponseDto> listBySubject(@PathVariable UUID subjectId) {
        UUID userId = getAuthenticatedUserId();
        return subjectModuleService.listBySubject(userId, subjectId);
    }

    @PostMapping
    public SubjectModuleResponseDto createModule(
            @PathVariable UUID subjectId,
            @RequestBody SubjectModuleCreateRequestDto request) {
        UUID userId = getAuthenticatedUserId();
        return subjectModuleService.createModule(userId, subjectId, request);
    }

    @PutMapping("/{moduleId}")
    public SubjectModuleResponseDto updateModule(
            @PathVariable UUID subjectId,
            @PathVariable UUID moduleId,
            @RequestBody SubjectModuleUpdateRequestDto request) {
        UUID userId = getAuthenticatedUserId();
        return subjectModuleService.updateModule(userId, subjectId, moduleId, request);
    }

    @DeleteMapping("/{moduleId}")
    public void deleteModule(
            @PathVariable UUID subjectId,
            @PathVariable UUID moduleId) {
        UUID userId = getAuthenticatedUserId();
        subjectModuleService.deleteModule(userId, subjectId, moduleId);
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
