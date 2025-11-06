package aktech.planificador.Dto.evento;

import java.util.List;

public class EventoRequestDto {
    private String titulo;
    private String color;
    private Integer idUsuario;
    private List<HorarioPorEventoDto> horarios;

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getIdUsuario() {
        return this.idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<HorarioPorEventoDto> getHorario() {
        return this.horarios;
    }

    public void setHorario(List<HorarioPorEventoDto> horarios) {
        this.horarios = horarios;
    }
}
