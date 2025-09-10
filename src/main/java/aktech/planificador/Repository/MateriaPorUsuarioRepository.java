package aktech.planificador.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aktech.planificador.Model.MateriaPorUsuario;

public interface MateriaPorUsuarioRepository extends JpaRepository<MateriaPorUsuario, Integer> {
    Optional<MateriaPorUsuario> findByIdUsuarioAndIdMateria(int idUsuario, int idMateria);
    Optional<MateriaPorUsuario> findByIdUsuario(int idUsuario);
}