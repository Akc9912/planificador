package aktech.planificador.modules.career.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import aktech.planificador.modules.career.enums.CareerStatus;

public class CareerResponseDto {
    private UUID id;
    private UUID userId;
    private String name;
    private String institution;
    private CareerStatus status;
    private LocalDate startDate;
    private boolean hasHours;
    private boolean hasCredits;
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

    public CareerStatus getStatus() {
        return status;
    }

    public void setStatus(CareerStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public boolean isHasHours() {
        return hasHours;
    }

    public void setHasHours(boolean hasHours) {
        this.hasHours = hasHours;
    }

    public boolean isHasCredits() {
        return hasCredits;
    }

    public void setHasCredits(boolean hasCredits) {
        this.hasCredits = hasCredits;
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
