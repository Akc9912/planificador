package aktech.planificador.modules.subject.presentation;

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

import aktech.planificador.modules.subject.application.SubjectModuleService;
import aktech.planificador.modules.subject.dto.SubjectModuleCreateRequestDto;
import aktech.planificador.modules.subject.dto.SubjectModuleResponseDto;
import aktech.planificador.modules.subject.dto.SubjectModuleUpdateRequestDto;
import aktech.planificador.shared.exception.BusinessException;

@RestController
@RequestMapping("/subjects/{subjectId}/modules")
public class SubjectModuleController {

    private final SubjectModuleService subjectModuleService;

    public SubjectModuleController(SubjectModuleService subjectModuleService) {
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
