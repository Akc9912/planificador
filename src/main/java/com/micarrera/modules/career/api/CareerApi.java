package com.micarrera.modules.career.api;

import java.util.UUID;

import com.micarrera.shared.dto.CareerBasicDto;

public interface CareerApi {

    CareerBasicDto getCareerBasic(UUID careerId);

    boolean existsCareer(UUID careerId);

    boolean userOwnsCareer(UUID userId, UUID careerId);
}
