package aktech.planificador.Repository.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.Model.historial.HorarioPorMateria;

import java.util.List;

@Repository
public interface HorarioPorMateriaRepository extends JpaRepository<HorarioPorMateria, Integer> {
    List<HorarioPorMateria> findByMateriaId(Integer materiaId);
}
