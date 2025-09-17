package aktech.planificador.DTO.materia;

import java.util.List;

import aktech.planificador.Model.core.Materia;
import aktech.planificador.Model.historial.HorarioPorMateria;

public class MateriaResponseDTO {
    private Materia materia;
    private List<HorarioPorMateria> horarios;

    public MateriaResponseDTO(Materia materia, List<HorarioPorMateria> horarios) {
        this.materia = materia;
        this.horarios = horarios;
    }

    public Materia getMateria() {
        return materia;
    }

    public List<HorarioPorMateria> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioPorMateria> horarios) {
        this.horarios = horarios;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }
}
