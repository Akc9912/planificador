package aktech.planificador.Service.core;

import java.util.List;
import java.util.Optional;
import aktech.planificador.Exception.ResourceNotFoundException;

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
        Usuario user = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Evento evento = new Evento(request.getTitulo(), request.getColor(), user);
        evento = eventoRepository.save(evento);

        // Si hay una materia asociada, crear la relación
        if (request.getIdMateria() != null) {
            Materia materia = materiaRepository.findById(request.getIdMateria())
                    .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));
            EventoMateria eventoMateria = new EventoMateria(evento, materia);
            eventoMateriaRepository.save(eventoMateria);
        }

        // Guardar los horarios del evento
        if (request.getHorarios() != null && !request.getHorarios().isEmpty()) {
            List<HorarioPorEvento> horarios = request.getHorarios().stream()
                    .map(horario -> new HorarioPorEvento(evento, horario.getInicio(), horario.getFin()))
                    .toList();
            horarioPorEventoRepository.saveAll(horarios);
        }

        return mapToDto(evento);
    }

    // editar evento
    public EventoResponseDto editarEvento(EventoRequestDto request, int idEvento) {
        Evento evento = eventoRepository.findById(idEvento);
        evento.setTitulo(request.getTitulo());
        evento.setColor(request.getColor());
        evento = eventoRepository.save(evento);

        // Manejar la relación con la materia
        List<EventoMateria> eventoMaterias = eventoMateriaRepository.findByEvento_Id(idEvento);

        // Si hay una materia en el request
        if (request.getIdMateria() != null) {
            Materia materia = materiaRepository.findById(request.getIdMateria())
                    .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));

            // Si no existe la relación, la creamos
            if (eventoMaterias.isEmpty()) {
                EventoMateria eventoMateria = new EventoMateria(evento, materia);
                eventoMateriaRepository.save(eventoMateria);
            }
            // Si existe y es diferente, la actualizamos
            else if (!eventoMaterias.get(0).getMateria().getId().equals(request.getIdMateria())) {
                EventoMateria eventoMateria = eventoMaterias.get(0);
                eventoMateria.setMateria(materia);
                eventoMateriaRepository.save(eventoMateria);
            }
        }
        // Si no hay materia en el request pero existe una relación, la eliminamos
        else if (!eventoMaterias.isEmpty()) {
            eventoMateriaRepository.deleteAll(eventoMaterias);
        }

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

        // Buscar si el evento tiene una materia asociada
        EventoMateria eventoMateria = eventoMateriaRepository.findByEvento_Id(evento.getId())
                .stream().findFirst().orElse(null);

        if (eventoMateria != null && eventoMateria.getMateria() != null) {
            dto.setIdMateria(eventoMateria.getMateria().getId());
            dto.setNombreMateria(eventoMateria.getMateria().getNombre());
        }

        return dto;
    }

    // mapear lista de eventos a dto
    public List<EventoResponseDto> mapToDto(List<Evento> eventos) {
        return eventos.stream().map(this::mapToDto).toList();
    }
}
