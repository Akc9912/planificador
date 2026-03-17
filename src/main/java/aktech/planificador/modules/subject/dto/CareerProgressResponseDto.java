package aktech.planificador.modules.subject.dto;

import java.util.UUID;

public class CareerProgressResponseDto {
    private UUID careerId;
    private int totalSubjects;
    private int approvedSubjects;
    private int pendingSubjects;
    private int inProgressSubjects;
    private int regularSubjects;
    private int libreSubjects;
    private int blockedSubjects;
    private int availableSubjects;
    private int totalCredits;
    private int approvedCredits;
    private int totalHours;
    private int approvedHours;
    private double progressPercentageBySubjects;
    private double progressPercentageByCredits;
    private double progressPercentageByHours;

    public UUID getCareerId() {
        return careerId;
    }

    public void setCareerId(UUID careerId) {
        this.careerId = careerId;
    }

    public int getTotalSubjects() {
        return totalSubjects;
    }

    public void setTotalSubjects(int totalSubjects) {
        this.totalSubjects = totalSubjects;
    }

    public int getApprovedSubjects() {
        return approvedSubjects;
    }

    public void setApprovedSubjects(int approvedSubjects) {
        this.approvedSubjects = approvedSubjects;
    }

    public int getPendingSubjects() {
        return pendingSubjects;
    }

    public void setPendingSubjects(int pendingSubjects) {
        this.pendingSubjects = pendingSubjects;
    }

    public int getInProgressSubjects() {
        return inProgressSubjects;
    }

    public void setInProgressSubjects(int inProgressSubjects) {
        this.inProgressSubjects = inProgressSubjects;
    }

    public int getRegularSubjects() {
        return regularSubjects;
    }

    public void setRegularSubjects(int regularSubjects) {
        this.regularSubjects = regularSubjects;
    }

    public int getLibreSubjects() {
        return libreSubjects;
    }

    public void setLibreSubjects(int libreSubjects) {
        this.libreSubjects = libreSubjects;
    }

    public int getBlockedSubjects() {
        return blockedSubjects;
    }

    public void setBlockedSubjects(int blockedSubjects) {
        this.blockedSubjects = blockedSubjects;
    }

    public int getAvailableSubjects() {
        return availableSubjects;
    }

    public void setAvailableSubjects(int availableSubjects) {
        this.availableSubjects = availableSubjects;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    public int getApprovedCredits() {
        return approvedCredits;
    }

    public void setApprovedCredits(int approvedCredits) {
        this.approvedCredits = approvedCredits;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public int getApprovedHours() {
        return approvedHours;
    }

    public void setApprovedHours(int approvedHours) {
        this.approvedHours = approvedHours;
    }

    public double getProgressPercentageBySubjects() {
        return progressPercentageBySubjects;
    }

    public void setProgressPercentageBySubjects(double progressPercentageBySubjects) {
        this.progressPercentageBySubjects = progressPercentageBySubjects;
    }

    public double getProgressPercentageByCredits() {
        return progressPercentageByCredits;
    }

    public void setProgressPercentageByCredits(double progressPercentageByCredits) {
        this.progressPercentageByCredits = progressPercentageByCredits;
    }

    public double getProgressPercentageByHours() {
        return progressPercentageByHours;
    }

    public void setProgressPercentageByHours(double progressPercentageByHours) {
        this.progressPercentageByHours = progressPercentageByHours;
    }
}
