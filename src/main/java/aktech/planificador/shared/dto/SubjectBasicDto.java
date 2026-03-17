package aktech.planificador.shared.dto;

import java.util.UUID;

public class SubjectBasicDto {
    private UUID id;
    private UUID careerId;
    private String name;
    private String code;
    private String status;

    public SubjectBasicDto() {
    }

    public SubjectBasicDto(UUID id, UUID careerId, String name, String code, String status) {
        this.id = id;
        this.careerId = careerId;
        this.name = name;
        this.code = code;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCareerId() {
        return careerId;
    }

    public void setCareerId(UUID careerId) {
        this.careerId = careerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
