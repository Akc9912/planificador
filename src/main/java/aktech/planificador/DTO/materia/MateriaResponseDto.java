package aktech.planificador.Dto.materia;

import java.util.List;

public class MateriaResponseDto {
    private Integer id;
    private String titulo;
    private String estado;
    private String color;
    private boolean promocionable;
    private Double notaPromocion;
    private Double calificacion;
    private List<HorarioMateriaResponseDto> horarios;

    // getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isPromocionable() {
        return promocionable;
    }

    public void setPromocionable(boolean promocionable) {
        this.promocionable = promocionable;
    }

    public Double getNotaPromocion() {
        return notaPromocion;
    }

    public void setNotaPromocion(Double notaPromocion) {
        this.notaPromocion = notaPromocion;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public List<HorarioMateriaResponseDto> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioMateriaResponseDto> horarios) {
        this.horarios = horarios;
    }
}
