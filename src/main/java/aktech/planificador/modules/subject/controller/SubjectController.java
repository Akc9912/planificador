package aktech.planificador.modules.subject.controller;

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

import aktech.planificador.modules.subject.dto.CareerProgressResponseDto;
import aktech.planificador.modules.subject.dto.SubjectAvailabilityResponseDto;
import aktech.planificador.modules.subject.dto.SubjectCreateRequestDto;
import aktech.planificador.modules.subject.dto.SubjectResponseDto;
import aktech.planificador.modules.subject.dto.SubjectUpdateRequestDto;
import aktech.planificador.modules.subject.service.SubjectService;
import aktech.planificador.shared.exception.BusinessException;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public SubjectResponseDto createSubject(@RequestBody SubjectCreateRequestDto request) {
        UUID userId = getAuthenticatedUserId();
        return subjectService.createSubject(userId, request);
    }

    @GetMapping("/career/{careerId}")
    public List<SubjectResponseDto> listByCareer(@PathVariable UUID careerId) {
        UUID userId = getAuthenticatedUserId();
        return subjectService.listByCareer(userId, careerId);
    }

    @GetMapping("/career/{careerId}/status/{status}")
    public List<SubjectResponseDto> listByCareerAndStatus(
            @PathVariable UUID careerId,
            @PathVariable String status) {
        UUID userId = getAuthenticatedUserId();
        return subjectService.listByCareerAndStatus(userId, careerId, status);
    }

    @GetMapping("/career/{careerId}/progress")
    public CareerProgressResponseDto getCareerProgress(@PathVariable UUID careerId) {
        UUID userId = getAuthenticatedUserId();
        return subjectService.getCareerProgress(userId, careerId);
    }

    @GetMapping("/{id}")
    public SubjectResponseDto getByIdOwned(@PathVariable UUID id) {
        UUID userId = getAuthenticatedUserId();
        return subjectService.getOwnedOrThrow(id, userId);
    }

    @GetMapping("/{id}/ownership")
    public boolean ownsSubject(@PathVariable UUID id) {
        UUID userId = getAuthenticatedUserId();
        return subjectService.ownsSubject(id, userId);
    }

    @GetMapping("/{id}/availability")
    public SubjectAvailabilityResponseDto getAvailability(@PathVariable UUID id) {
        UUID userId = getAuthenticatedUserId();
        return subjectService.getAvailability(id, userId);
    }

    @GetMapping("/{id}/unlocks/{status}")
    public List<SubjectResponseDto> listUnlockedByStatusChange(
            @PathVariable UUID id,
            @PathVariable String status) {
        UUID userId = getAuthenticatedUserId();
        return subjectService.listUnlockedByStatusChange(id, userId, status);
    }

    @PutMapping("/{id}")
    public SubjectResponseDto updateSubject(
            @PathVariable UUID id,
            @RequestBody SubjectUpdateRequestDto request) {
        UUID userId = getAuthenticatedUserId();
        return subjectService.updateSubject(id, userId, request);
    }

    @DeleteMapping("/{id}")
    public void deleteSubject(@PathVariable UUID id) {
        UUID userId = getAuthenticatedUserId();
        subjectService.deleteSubject(id, userId);
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
