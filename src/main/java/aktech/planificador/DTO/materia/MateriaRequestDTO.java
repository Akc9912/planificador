package aktech.planificador.Dto.materia;

import java.util.List;

public class MateriaRequestDto {
    private Integer usuarioId;
    private String titulo;
    private String color;
    private boolean promocionable;
    private Double notaPromocion;
    private Double calificacion;
    private String estado;
    private List<HorarioMateriaRequestDto> horarios;

    public List<HorarioMateriaRequestDto> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioMateriaRequestDto> horarios) {
        this.horarios = horarios;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public Double getNotaPromocion() {
        return notaPromocion;
    }

    public void setNotaPromocion(Double notaPromocion) {
        this.notaPromocion = notaPromocion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
