package aktech.planificador.Dto.materia;

import java.util.List;

public class DashboardDataDto {
    private List<MateriaPlannerResponseDto> proximasMaterias;
    private Long materiasActivas;
    private Long horasSemanales;

    // Getters y Setters

    public List<MateriaPlannerResponseDto> getProximasMaterias() {
        return proximasMaterias;
    }

    public void setProximasMaterias(List<MateriaPlannerResponseDto> proximasMaterias) {
        this.proximasMaterias = proximasMaterias;
    }

    public Long getMateriasActivas() {
        return materiasActivas;
    }

    public void setMateriasActivas(Long materiasActivas) {
        this.materiasActivas = materiasActivas;
    }

    public Long getHorasSemanales() {
        return horasSemanales;
    }

    public void setHorasSemanales(Long horasSemanales) {
        this.horasSemanales = horasSemanales;
    }
}
