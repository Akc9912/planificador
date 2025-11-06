package aktech.planificador.Service.core;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import aktech.planificador.Dto.evento.EventoRequestDto;
import aktech.planificador.Dto.evento.EventoResponseDto;
import aktech.planificador.Model.core.Evento;
import aktech.planificador.Model.core.Materia;
import aktech.planificador.Model.core.Usuario;
import aktech.planificador.Model.historial.EventoMateria;
import aktech.planificador.Model.historial.HorarioPorEvento;
import aktech.planificador.Repository.core.EventoRepository;
import aktech.planificador.Repository.core.MateriaRepository;
import aktech.planificador.Repository.core.UsuarioRepository;
import aktech.planificador.Repository.historial.EventoMateriaRepository;
import aktech.planificador.Repository.historial.HorarioPorEventoRepository;

@Service
public class EventoService {
    public final EventoRepository eventoRepository;
    public final UsuarioRepository usuarioRepository;
    public final MateriaRepository materiaRepository;
    public final EventoMateriaRepository eventoMateriaRepository;
    public final HorarioPorEventoRepository horarioPorEventoRepository;

    public EventoService(EventoRepository eventoRepository, UsuarioRepository usuarioRepository,
            MateriaRepository materiaRepository, EventoMateriaRepository eventoMateriaRepository,
            HorarioPorEventoRepository horarioPorEventoRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.materiaRepository = materiaRepository;
        this.eventoMateriaRepository = eventoMateriaRepository;
        this.horarioPorEventoRepository = horarioPorEventoRepository;
    }

    // crear evento y sus horarios
    public EventoResponseDto crearEvento(EventoRequestDto request) {
        Optional<Usuario> user = usuarioRepository.findById(request.getIdUsuario());
        Evento evento = new Evento(request.getTitulo(), request.getColor(), user);
        if (evento != null) {
            // si el evento no es nulo creamos la lista de horarios del evento si no es nulo a partir de la lista de horarios del request
            List<HorarioPorEvento> horarios = horarioPorEventoRepository
                    .saveAll(request.getHorario().stream().map(horario -> new HorarioPorEvento(evento, horario.getInicio(), horario.getFin())).toList());
                            .toList());
        }
        return mapToDto(evento);
    }

    // editar evento
    public EventoResponseDto editarEvento(EventoRequestDto request, int idEvento) {
        Evento evento = eventoRepository.findById(idEvento);
        evento.setTitulo(request.getTitulo());
        evento.setColor(request.getColor());
        eventoRepository.save(evento);
        return mapToDto(evento);
    }

    // eliminar evento
    public void eliminarEvento(int idEvento) {
        eventoRepository.deleteById(idEvento);
    }

    // ----------- Utilidades --------------------

    // mapear evento a dto
    public EventoResponseDto mapToDto(Evento evento) {
        EventoResponseDto dto = new EventoResponseDto();
        dto.setId(evento.getId());
        dto.setTitulo(evento.getTitulo());
        dto.setColor(evento.getColor());
        return dto;
    }

    // mapear lista de eventos a dto
    public List<EventoResponseDto> mapToDto(List<Evento> eventos) {
        return eventos.stream().map(this::mapToDto).toList();
    }
}
