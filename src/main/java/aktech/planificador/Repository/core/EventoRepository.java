package aktech.planificador.Repository.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aktech.planificador.Model.core.Evento;
import aktech.planificador.Model.core.Usuario;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {

    List<Evento> findByUsuario(Usuario u);

    List<Evento> findByUsuarioId(Integer idUsuario);

    Evento findById(int id);
}
