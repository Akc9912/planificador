package aktech.planificador.Repository.sopoerte;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aktech.planificador.Model.enums.SoporteTipo;
import aktech.planificador.Model.soporte.Soporte;

public interface SoporteRepository extends JpaRepository<Soporte, Integer> {
    List<Soporte> findByTipo(SoporteTipo tipo);

    List<Soporte> findByIdUsuario(int idUsuario);

    List<Soporte> findByIdUsuarioAndTipo(int idUsuario, SoporteTipo tipo);

    Soporte findById(int id);
}
