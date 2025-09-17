package aktech.planificador.Service.core;

import aktech.planificador.DTO.materia.HorarioMateriaRequest;
import aktech.planificador.Model.enums.DiaSemana;
import aktech.planificador.Model.historial.HorarioPorMateria;
import aktech.planificador.Repository.historial.HorarioPorMateriaRepository;

import aktech.planificador.Model.core.Materia;
import aktech.planificador.Model.core.Usuario;
import aktech.planificador.Model.enums.EstadoMateria;
import aktech.planificador.Repository.core.MateriaRepository;
import aktech.planificador.Repository.core.UsuarioRepository;
import aktech.planificador.DTO.materia.MateriaRequestDTO;
import aktech.planificador.DTO.materia.MateriaResponseDTO;
import aktech.planificador.DTO.GenericResponseDTO;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MateriaService {
    private final MateriaRepository materiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioPorMateriaRepository horarioRepository;

    public MateriaService(MateriaRepository materiaRepository, UsuarioRepository usuarioRepository,
            HorarioPorMateriaRepository horarioRepository) {
        this.materiaRepository = materiaRepository;
        this.usuarioRepository = usuarioRepository;
        this.horarioRepository = horarioRepository;
    }

    public GenericResponseDTO crearMateria(MateriaRequestDTO request) {
        try {
            // Validar datos obligatorios
            if (request.getUsuarioId() == null) {
                return new GenericResponseDTO("El usuario es obligatorio", false);
            }
            if (request.getTitulo() == null || request.getTitulo().isEmpty()) {
                return new GenericResponseDTO("El título es obligatorio", false);
            }
            if (request.getColor() == null || request.getColor().isEmpty()) {
                return new GenericResponseDTO("El color es obligatorio", false);
            }
            if (request.getEstado() == null) {
                return new GenericResponseDTO("El estado es obligatorio", false);
            }

            Optional<Usuario> usuarioOpt = usuarioRepository.findById(request.getUsuarioId());
            if (usuarioOpt.isEmpty()) {
                return new GenericResponseDTO("Usuario no encontrado", false);
            }

            Materia materia = new Materia();
            materia.setUsuario(usuarioOpt.get());
            materia.setTitulo(request.getTitulo());
            materia.setColor(request.getColor());
            materia.setPromocionable(request.isPromocionable());
            materia.setNotaPromocion(request.getNotaPromocion());
            materia.setCalificacion(request.getCalificacion());
            // convertir el string estado a enum EstadoMateria
            EstadoMateria estado = EstadoMateria.valueOf(request.getEstado().toUpperCase());
            materia.setEstado(estado);

            materiaRepository.save(materia);

            // Crear horarios si vienen en el request
            if (request.getHorarios() != null && !request.getHorarios().isEmpty()) {
                for (HorarioMateriaRequest horarioReq : request.getHorarios()) {
                    HorarioPorMateria horario = new HorarioPorMateria();
                    horario.setMateria(materia);
                    horario.setDia(DiaSemana.valueOf(horarioReq.getDia().toUpperCase()));
                    horario.setHoraInicio(java.time.LocalTime.parse(horarioReq.getHoraInicio()));
                    horario.setHoraFin(java.time.LocalTime.parse(horarioReq.getHoraFin()));
                    horarioRepository.save(horario);
                }
            }
            return new GenericResponseDTO("Materia creada exitosamente", true);
        } catch (Exception e) {
            return new GenericResponseDTO("Error al crear la materia: " + e.getMessage(), false);
        }
    }

    public GenericResponseDTO modificarMateria(Integer id, MateriaRequestDTO request) {
        try {
            Optional<Materia> materiaOpt = materiaRepository.findById(id);
            if (materiaOpt.isEmpty()) {
                return new GenericResponseDTO("Materia no encontrada", false);
            }
            Materia materia = materiaOpt.get();

            if (request.getTitulo() != null && !request.getTitulo().isEmpty()) {
                materia.setTitulo(request.getTitulo());
            }
            if (request.getColor() != null && !request.getColor().isEmpty()) {
                materia.setColor(request.getColor());
            }
            materia.setPromocionable(request.isPromocionable());
            if (request.getNotaPromocion() != null) {
                materia.setNotaPromocion(request.getNotaPromocion());
            }
            if (request.getCalificacion() != null) {
                materia.setCalificacion(request.getCalificacion());
            }
            if (request.getEstado() != null) {
                EstadoMateria estado = EstadoMateria.valueOf(request.getEstado().toUpperCase());
                materia.setEstado(estado);
            }
            if (request.getNotaPromocion() != null) {
                materia.setNotaPromocion(request.getNotaPromocion());
            }

            materiaRepository.save(materia);
            return new GenericResponseDTO("Materia modificada exitosamente", true);
        } catch (Exception e) {
            return new GenericResponseDTO("Error al modificar la materia: " + e.getMessage(), false);
        }
    }

    public GenericResponseDTO eliminarMateria(Integer id) {
        try {
            if (!materiaRepository.existsById(id)) {
                return new GenericResponseDTO("Materia no encontrada", false);
            }
            materiaRepository.deleteById(id);
            return new GenericResponseDTO("Materia eliminada exitosamente", true);
        } catch (Exception e) {
            return new GenericResponseDTO("Error al eliminar la materia: " + e.getMessage(), false);
        }
    }

    // obtener lista de materias por usuario
    public List<MateriaResponseDTO> obtenerMateriasPorUsuario(Integer usuarioId) {
        try {
            if (usuarioId == null) {
                return List.of();
            }
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return List.of();
            }
            List<Materia> materias = materiaRepository.findByUsuarioId(usuarioId);
            return materias.stream()
                    .map(m -> new MateriaResponseDTO(m, null))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    // Agregar uno o varios horarios a una materia existente
    public GenericResponseDTO agregarHorariosAMateria(Integer materiaId, List<HorarioMateriaRequest> horariosRequest) {
        try {
            Optional<Materia> materiaOpt = materiaRepository.findById(materiaId);
            if (materiaOpt.isEmpty()) {
                return new GenericResponseDTO("Materia no encontrada", false);
            }
            Materia materia = materiaOpt.get();
            if (horariosRequest == null || horariosRequest.isEmpty()) {
                return new GenericResponseDTO("No se recibieron horarios para agregar", false);
            }
            for (HorarioMateriaRequest req : horariosRequest) {
                HorarioPorMateria horario = new HorarioPorMateria();
                horario.setMateria(materia);
                horario.setDia(DiaSemana.valueOf(req.getDia().toUpperCase()));
                horario.setHoraInicio(java.time.LocalTime.parse(req.getHoraInicio()));
                horario.setHoraFin(java.time.LocalTime.parse(req.getHoraFin()));
                horarioRepository.save(horario);
            }
            return new GenericResponseDTO("Horarios agregados exitosamente", true);
        } catch (Exception e) {
            return new GenericResponseDTO("Error al agregar horarios: " + e.getMessage(), false);
        }
    }

    // lista de materias con horario, para el planificador (si una materia tiene
    // varios horarios, se repite la materia en cada horario)
    public List<MateriaResponseDTO> obtenerMateriasConHorariosPorUsuario(Integer usuarioId) {
        try {
            if (usuarioId == null) {
                return List.of();
            }
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return List.of();
            }
            List<Materia> materias = materiaRepository.findByUsuarioId(usuarioId);
            return materias.stream()
                    .map(m -> {
                        List<HorarioPorMateria> horarios = horarioRepository.findByMateriaId(m.getId());
                        return new MateriaResponseDTO(m, horarios);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
}
