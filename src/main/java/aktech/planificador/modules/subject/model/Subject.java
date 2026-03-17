package aktech.planificador.modules.subject.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "career_id", nullable = false)
    private UUID careerId;

    @Column(nullable = false)
    private String name;

    @Column
    private String code;

    @Column(nullable = false)
    private String status;

    @Column
    private Integer grade;

    @Column(name = "approval_method")
    private String approvalMethod;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "correlatives", columnDefinition = "text[]")
    private String[] correlatives = new String[0];

    @Column(name = "year")
    private Integer year;

    @Column(name = "semester")
    private Integer semester;

    @Column(name = "is_entrance_course", nullable = false)
    private boolean entranceCourse = false;

    @Column(name = "hours")
    private Integer hours;

    @Column(name = "credits")
    private Integer credits;

    @Column(nullable = false)
    private String color = "#3B82F6";

    @Column(name = "grade_required_for_promotion", precision = 4, scale = 2)
    private BigDecimal gradeRequiredForPromotion;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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

    public String[] getCorrelatives() {
        return correlatives;
    }

    public void setCorrelatives(String[] correlatives) {
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

    public boolean isEntranceCourse() {
        return entranceCourse;
    }

    public void setEntranceCourse(boolean entranceCourse) {
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
