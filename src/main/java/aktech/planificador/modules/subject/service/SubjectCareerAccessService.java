package aktech.planificador.modules.subject.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import aktech.planificador.shared.api.CareerApi;
import aktech.planificador.shared.exception.NotFoundException;

@Service
public class SubjectCareerAccessService {

    private final CareerApi careerApi;

    public SubjectCareerAccessService(CareerApi careerApi) {
        this.careerApi = careerApi;
    }

    public void validateCareerOwnership(UUID userId, UUID careerId) {
        if (!careerApi.existsCareer(careerId)) {
            throw new NotFoundException("Carrera no encontrada");
        }

        if (!careerApi.userOwnsCareer(userId, careerId)) {
            throw new NotFoundException("La carrera no pertenece al usuario");
        }
    }
}
