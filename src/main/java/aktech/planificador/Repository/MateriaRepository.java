package aktech.planificador.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aktech.planificador.Model.Materia;

public interface MateriaRepository extends JpaRepository<Materia, Integer> {

}
