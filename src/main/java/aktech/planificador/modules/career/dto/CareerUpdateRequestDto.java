package aktech.planificador.modules.career.dto;

import java.time.LocalDate;

import aktech.planificador.modules.career.enums.CareerStatus;

public class CareerUpdateRequestDto {
    private String name;
    private String institution;
    private LocalDate startDate;
    private CareerStatus status;
    private Boolean hasHours;
    private Boolean hasCredits;

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public CareerStatus getStatus() {
        return status;
    }

    public void setStatus(CareerStatus status) {
        this.status = status;
    }

    public Boolean getHasHours() {
        return hasHours;
    }

    public void setHasHours(Boolean hasHours) {
        this.hasHours = hasHours;
    }

    public Boolean getHasCredits() {
        return hasCredits;
    }

    public void setHasCredits(Boolean hasCredits) {
        this.hasCredits = hasCredits;
    }
}
