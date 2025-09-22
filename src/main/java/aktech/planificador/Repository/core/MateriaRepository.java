package aktech.planificador.Repository.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.Model.core.Materia;
import aktech.planificador.Model.enums.EstadoMateria;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Integer> {
    List<Materia> findByUsuarioId(Integer usuarioId);

    Long countByUsuarioIdAndEstado(Integer usuarioId, EstadoMateria estado);
}
