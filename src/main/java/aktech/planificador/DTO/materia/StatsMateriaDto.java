package aktech.planificador.Dto.materia;

public class StatsMateriaDto {
    private Long materiasActivas;
    private Long materiasTotales;
    private Double promedio;

    public Long getMateriasActivas() {
        return materiasActivas;
    }

    public void setMateriasActivas(Long materiasActivas) {
        this.materiasActivas = materiasActivas;
    }

    public Long getMateriasTotales() {
        return materiasTotales;
    }

    public void setMateriasTotales(Long materiasTotales) {
        this.materiasTotales = materiasTotales;
    }

    public Double getPromedio() {
        return promedio;
    }

    public void setPromedio(Double promedio) {
        this.promedio = promedio;
    }
}
