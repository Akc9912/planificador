package aktech.planificador.Repository.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.Model.historial.HorarioPorEvento;

@Repository
public interface HorarioPorEventoRepository extends JpaRepository<HorarioPorEvento, Integer> {
    // Métodos personalizados si es necesario
}
