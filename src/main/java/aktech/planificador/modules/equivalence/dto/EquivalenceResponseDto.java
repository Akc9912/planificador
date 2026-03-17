package aktech.planificador.modules.equivalence.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class EquivalenceResponseDto {
    private UUID id;
    private UUID userId;
    private String type;
    private UUID subjectAId;
    private UUID subjectBId;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getSubjectAId() {
        return subjectAId;
    }

    public void setSubjectAId(UUID subjectAId) {
        this.subjectAId = subjectAId;
    }

    public UUID getSubjectBId() {
        return subjectBId;
    }

    public void setSubjectBId(UUID subjectBId) {
        this.subjectBId = subjectBId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
