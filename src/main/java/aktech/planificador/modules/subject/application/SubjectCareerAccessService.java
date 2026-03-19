package aktech.planificador.modules.subject.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import aktech.planificador.modules.subject.domain.model.Subject;
import aktech.planificador.modules.subject.persistence.SubjectRepository;
import aktech.planificador.shared.api.CareerApi;
import aktech.planificador.shared.exception.NotFoundException;
import aktech.planificador.shared.util.ValidationUtils;

@Service
public class SubjectCareerAccessService {

    private final CareerApi careerApi;
    private final SubjectRepository subjectRepository;

    public SubjectCareerAccessService(CareerApi careerApi, SubjectRepository subjectRepository) {
        this.careerApi = careerApi;
        this.subjectRepository = subjectRepository;
    }

    public void validateCareerOwnership(UUID userId, UUID careerId) {
        ValidationUtils.requireNotNull(userId, "El id de usuario es obligatorio");
        ValidationUtils.requireNotNull(careerId, "El id de carrera es obligatorio");

        if (!careerApi.existsCareer(careerId)) {
            throw new NotFoundException("Carrera no encontrada");
        }

        if (!careerApi.userOwnsCareer(userId, careerId)) {
            throw new NotFoundException("La carrera no pertenece al usuario");
        }
    }

    public Subject getOwnedSubjectOrThrow(UUID userId, UUID subjectId) {
        ValidationUtils.requireNotNull(userId, "El id de usuario es obligatorio");
        ValidationUtils.requireNotNull(subjectId, "El id de materia es obligatorio");

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundException("Materia no encontrada o sin permisos"));

        if (!careerApi.userOwnsCareer(userId, subject.getCareerId())) {
            throw new NotFoundException("Materia no encontrada o sin permisos");
        }

        return subject;
    }

    public boolean userOwnsSubject(UUID userId, UUID subjectId) {
        Subject subject = subjectRepository.findById(subjectId).orElse(null);
        if (subject == null) {
            return false;
        }

        return careerApi.userOwnsCareer(userId, subject.getCareerId());
    }
}
