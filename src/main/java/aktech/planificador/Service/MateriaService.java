package aktech.planificador.Service;

import aktech.planificador.DTO.MateriaRequestDto;
import aktech.planificador.Model.Materia;
import aktech.planificador.Model.enums.EstadoMateria;
import aktech.planificador.Repository.MateriaRepository;

public class MateriaService {
    private final MateriaRepository materiaRepository;

    public MateriaService(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    // crear materia
    public String crearMateria(MateriaRequestDto request) {
        Materia materia = new Materia();
        materia.setTitulo(request.getTitulo());
        materia.setEstado(EstadoMateria.valueOf(request.getEstado()));
        materia.setColor(request.getColor());
        materiaRepository.save(materia);
        return "Materia creada exitosamente";
    }

    // actualizar materia
    public String actualizarMateria(int id, MateriaRequestDto request) {
        Materia materia = materiaRepository.findById(id).orElse(null);
        if (materia == null)  return "Materia no encontrada";
        materia.setTitulo(request.getTitulo());
        materia.setEstado(EstadoMateria.valueOf(request.getEstado()));
        materia.setColor(request.getColor());
        materiaRepository.save(materia);
        return "Materia actualizada exitosamente";
    }
}
