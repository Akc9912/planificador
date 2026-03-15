package aktech.planificador.shared.dto;

import java.util.UUID;

public class CareerBasicDto {
    private UUID id;
    private UUID userId;
    private String name;
    private String institution;

    public CareerBasicDto() {
    }

    public CareerBasicDto(UUID id, UUID userId, String name, String institution) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.institution = institution;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }
}
