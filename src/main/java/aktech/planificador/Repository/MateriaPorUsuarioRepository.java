package aktech.planificador.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aktech.planificador.Model.MateriaPorUsuario;

public interface MateriaPorUsuarioRepository extends JpaRepository<MateriaPorUsuario, Integer> {
    Optional<MateriaPorUsuario> findByUsuarioIdAndMateriaId(int usuarioId, int materiaId);
    Optional<MateriaPorUsuario> findByUsuarioId(int usuarioId);
}