package aktech.planificador.modules.subject.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SubjectUpdateRequestDto {
    private String name;
    private String code;
    private String status;
    private Integer grade;
    private String approvalMethod;
    private List<UUID> correlatives;
    private Integer year;
    private Integer semester;
    private Boolean entranceCourse;
    private Integer hours;
    private Integer credits;
    private String color;
    private BigDecimal gradeRequiredForPromotion;

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

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getApprovalMethod() {
        return approvalMethod;
    }

    public void setApprovalMethod(String approvalMethod) {
        this.approvalMethod = approvalMethod;
    }

    public List<UUID> getCorrelatives() {
        return correlatives;
    }

    public void setCorrelatives(List<UUID> correlatives) {
        this.correlatives = correlatives;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Boolean getEntranceCourse() {
        return entranceCourse;
    }

    public void setEntranceCourse(Boolean entranceCourse) {
        this.entranceCourse = entranceCourse;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getGradeRequiredForPromotion() {
        return gradeRequiredForPromotion;
    }

    public void setGradeRequiredForPromotion(BigDecimal gradeRequiredForPromotion) {
        this.gradeRequiredForPromotion = gradeRequiredForPromotion;
    }
}
