package aktech.planificador.shared.api;

import java.util.UUID;

import aktech.planificador.shared.dto.CareerBasicDto;

public interface CareerApi {

    CareerBasicDto getCareerBasic(UUID careerId);

    boolean existsCareer(UUID careerId);

    boolean userOwnsCareer(UUID userId, UUID careerId);
}
