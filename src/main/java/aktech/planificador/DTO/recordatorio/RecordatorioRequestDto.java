package aktech.planificador.Dto.recordatorio;

import java.util.List;

public class RecordatorioRequestDto {
    private Integer usuarioId;
    private String titulo;
    private String color;
    private List<Integer> materiaIds;

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

    public List<Integer> getMateriaIds() {
        return materiaIds;
    }

    public void setMateriaIds(List<Integer> materiaIds) {
        this.materiaIds = materiaIds;
    }
}
