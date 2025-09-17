package aktech.planificador.Repository.historial;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.Model.historial.EventoMateria;

@Repository
public interface EventoMateriaRepository extends JpaRepository<EventoMateria, Integer> {
    List<EventoMateria> findByEvento_Id(Integer eventoId);

    List<EventoMateria> findByMateria_Id(Integer materiaId);
}
