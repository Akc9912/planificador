package aktech.planificador.modules.equivalence.presentation;

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

import aktech.planificador.modules.equivalence.application.EquivalenceService;
import aktech.planificador.modules.equivalence.dto.EquivalenceCreateRequestDto;
import aktech.planificador.modules.equivalence.dto.EquivalenceResponseDto;
import aktech.planificador.modules.equivalence.dto.EquivalenceUpdateRequestDto;
import aktech.planificador.shared.exception.BusinessException;

@RestController
@RequestMapping("/equivalences")
public class EquivalenceController {

    private final EquivalenceService equivalenceService;

    public EquivalenceController(EquivalenceService equivalenceService) {
        this.equivalenceService = equivalenceService;
    }

    @PostMapping
    public EquivalenceResponseDto create(@RequestBody EquivalenceCreateRequestDto request) {
        UUID userId = getAuthenticatedUserId();
        return equivalenceService.createEquivalence(userId, request);
    }

    @GetMapping
    public List<EquivalenceResponseDto> listByUser() {
        UUID userId = getAuthenticatedUserId();
        return equivalenceService.listByUser(userId);
    }

    @GetMapping("/subject/{subjectId}")
    public List<EquivalenceResponseDto> listBySubject(@PathVariable UUID subjectId) {
        UUID userId = getAuthenticatedUserId();
        return equivalenceService.listBySubject(userId, subjectId);
    }

    @GetMapping("/{id}")
    public EquivalenceResponseDto getOwned(@PathVariable UUID id) {
        UUID userId = getAuthenticatedUserId();
        return equivalenceService.getOwnedOrThrow(id, userId);
    }

    @PutMapping("/{id}")
    public EquivalenceResponseDto update(
            @PathVariable UUID id,
            @RequestBody EquivalenceUpdateRequestDto request) {
        UUID userId = getAuthenticatedUserId();
        return equivalenceService.updateEquivalence(id, userId, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        UUID userId = getAuthenticatedUserId();
        equivalenceService.deleteEquivalence(id, userId);
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
