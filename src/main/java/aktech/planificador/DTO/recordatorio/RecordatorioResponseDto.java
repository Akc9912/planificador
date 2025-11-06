package aktech.planificador.Dto.recordatorio;

import java.util.List;
import aktech.planificador.Dto.materia.MateriaResponseDto;

public class RecordatorioResponseDto {
    private Integer id;
    private String titulo;
    private String color;
    private List<MateriaResponseDto> materias;

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<MateriaResponseDto> getMaterias() {
        return materias;
    }

    public void setMaterias(List<MateriaResponseDto> materias) {
        this.materias = materias;
    }
}
