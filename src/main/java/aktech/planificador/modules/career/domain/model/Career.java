package aktech.planificador.modules.career.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import aktech.planificador.modules.career.enums.CareerStatus;

@Entity
@Table(name = "careers")
public class Career {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String institution;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CareerStatus status = CareerStatus.NOT_STARTED;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "has_hours", nullable = false)
    private boolean hasHours = false;

    @Column(name = "has_credits", nullable = false)
    private boolean hasCredits = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Career() {
    }

    public Career(UUID userId, String name, String institution, LocalDate startDate, boolean hasHours,
            boolean hasCredits) {
        this.userId = userId;
        this.name = name;
        this.institution = institution;
        this.startDate = startDate;
        this.hasHours = hasHours;
        this.hasCredits = hasCredits;
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
