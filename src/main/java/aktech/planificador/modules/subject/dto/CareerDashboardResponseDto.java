package aktech.planificador.modules.subject.dto;

import java.util.List;
import java.util.UUID;

public class CareerDashboardResponseDto {
    private UUID careerId;
    private List<SubjectDashboardItemDto> currentSubjects;
    private List<SubjectDashboardItemDto> recommendedSubjects;
    private CareerProgressResponseDto progress;
    private int activeSubjects;
    private int weeklyHours;
    private List<String> alerts;

    public UUID getCareerId() {
        return careerId;
    }

    public void setCareerId(UUID careerId) {
        this.careerId = careerId;
    }

    public List<SubjectDashboardItemDto> getCurrentSubjects() {
        return currentSubjects;
    }

    public void setCurrentSubjects(List<SubjectDashboardItemDto> currentSubjects) {
        this.currentSubjects = currentSubjects;
    }

    public List<SubjectDashboardItemDto> getRecommendedSubjects() {
        return recommendedSubjects;
    }

    public void setRecommendedSubjects(List<SubjectDashboardItemDto> recommendedSubjects) {
        this.recommendedSubjects = recommendedSubjects;
    }

    public CareerProgressResponseDto getProgress() {
        return progress;
    }

    public void setProgress(CareerProgressResponseDto progress) {
        this.progress = progress;
    }

    public int getActiveSubjects() {
        return activeSubjects;
    }

    public void setActiveSubjects(int activeSubjects) {
        this.activeSubjects = activeSubjects;
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(int weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public List<String> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<String> alerts) {
        this.alerts = alerts;
    }
}
