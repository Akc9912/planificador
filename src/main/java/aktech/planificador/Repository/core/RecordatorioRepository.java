package aktech.planificador.Repository.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.Model.core.Recordatorio;

@Repository
public interface RecordatorioRepository extends JpaRepository<Recordatorio, Integer> {
    // Métodos personalizados si es necesario
}
