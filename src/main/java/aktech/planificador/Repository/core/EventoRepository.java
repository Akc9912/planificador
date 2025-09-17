package aktech.planificador.Repository.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.Model.core.Evento;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {
    // Métodos personalizados si es necesario
}
