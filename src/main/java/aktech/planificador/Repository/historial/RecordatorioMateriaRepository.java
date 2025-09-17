package aktech.planificador.Repository.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.Model.historial.RecordatorioMateria;

@Repository
public interface RecordatorioMateriaRepository extends JpaRepository<RecordatorioMateria, Integer> {
    java.util.List<RecordatorioMateria> findByRecordatorio_Id(Integer recordatorioId);

    java.util.List<RecordatorioMateria> findByMateria_Id(Integer materiaId);
}
