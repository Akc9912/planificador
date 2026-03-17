package aktech.planificador.modules.subject.dto;

import java.math.BigDecimal;

public class SubjectModuleUpdateRequestDto {
    private String name;
    private BigDecimal grade;
    private Integer moduleOrder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    public Integer getModuleOrder() {
        return moduleOrder;
    }

    public void setModuleOrder(Integer moduleOrder) {
        this.moduleOrder = moduleOrder;
    }
}
