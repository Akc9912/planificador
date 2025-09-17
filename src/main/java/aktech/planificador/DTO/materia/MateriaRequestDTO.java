package aktech.planificador.DTO.materia;

import java.util.List;

public class MateriaRequestDTO {
    private Integer usuarioId;
    private String titulo;
    private String color;
    private boolean promocionable;
    private Double notaPromocion;
    private Double calificacion;
    private String estado;
    private List<HorarioMateriaRequest> horarios;

    public MateriaRequestDTO() {
    }

    public MateriaRequestDTO(Integer usuarioId, String titulo, String color, boolean promocionable,
            Double notaPromocion, Double calificacion, String estado, List<HorarioMateriaRequest> horarios) {
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.color = color;
        this.promocionable = promocionable;
        this.notaPromocion = notaPromocion;
        this.calificacion = calificacion;
        this.estado = estado;
        this.horarios = horarios;
    }

    public List<HorarioMateriaRequest> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioMateriaRequest> horarios) {
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
