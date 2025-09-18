package aktech.planificador.Service.core;

import aktech.planificador.Model.enums.DiaSemana;
import aktech.planificador.Model.historial.HorarioPorMateria;
import aktech.planificador.Repository.historial.HorarioPorMateriaRepository;
import aktech.planificador.Dto.GenericResponseDto;
import aktech.planificador.Dto.materia.HorarioMateriaRequestDto;
import aktech.planificador.Dto.materia.MateriaRequestDto;
import aktech.planificador.Dto.materia.MateriaResponseDto;
import aktech.planificador.Dto.materia.MateriaPlannerResponseDto;
import aktech.planificador.Model.core.Materia;
import aktech.planificador.Model.core.Usuario;
import aktech.planificador.Model.enums.EstadoMateria;
import aktech.planificador.Repository.core.MateriaRepository;
import aktech.planificador.Repository.core.UsuarioRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public GenericResponseDto crearMateria(MateriaRequestDto request) {
        try {
            GenericResponseDto response = new GenericResponseDto();
            // Validar datos obligatorios
            if (request.getUsuarioId() == null) {
                response.setMessage("El usuario es obligatorio");
                response.setSuccess(false);
                return response;
            }
            if (request.getTitulo() == null || request.getTitulo().isEmpty()) {
                response.setMessage("El título es obligatorio");
                response.setSuccess(false);
                return response;
            }
            if (request.getColor() == null || request.getColor().isEmpty()) {
                response.setMessage("El color es obligatorio");
                response.setSuccess(false);
                return response;
            }
            if (request.getEstado() == null) {
                response.setMessage("El estado es obligatorio");
                response.setSuccess(false);
                return response;
            }

            Optional<Usuario> usuarioOpt = usuarioRepository.findById(request.getUsuarioId());
            if (usuarioOpt.isEmpty()) {
                response.setMessage("Usuario no encontrado");
                response.setSuccess(false);
                return response;
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
                for (HorarioMateriaRequestDto horarioReq : request.getHorarios()) {
                    HorarioPorMateria horario = new HorarioPorMateria();
                    horario.setMateria(materia);
                    horario.setDia(DiaSemana.valueOf(horarioReq.getDia().toUpperCase()));
                    horario.setHoraInicio(java.time.LocalTime.parse(horarioReq.getHoraInicio()));
                    horario.setHoraFin(java.time.LocalTime.parse(horarioReq.getHoraFin()));
                    horarioRepository.save(horario);
                }
            }
            response.setMessage("Materia creada exitosamente");
            response.setSuccess(true);
            return response;
        } catch (Exception e) {
            GenericResponseDto response = new GenericResponseDto();
            response.setMessage("Error al crear la materia: " + e.getMessage());
            response.setSuccess(false);
            return response;
        }
    }

    public GenericResponseDto modificarMateria(Integer id, MateriaRequestDto request) {
        try {
            GenericResponseDto response = new GenericResponseDto();
            Optional<Materia> materiaOpt = materiaRepository.findById(id);
            if (materiaOpt.isEmpty()) {
                response.setMessage("Materia no encontrada");
                response.setSuccess(false);
                return response;
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
            response.setMessage("Materia modificada exitosamente");
            response.setSuccess(true);
            return response;
        } catch (Exception e) {
            GenericResponseDto response = new GenericResponseDto();
            response.setMessage("Error al modificar la materia: " + e.getMessage());
            response.setSuccess(false);
            return response;
        }
    }

    public GenericResponseDto eliminarMateria(Integer id) {
        try {
            GenericResponseDto response = new GenericResponseDto();
            if (!materiaRepository.existsById(id)) {
                response.setMessage("Materia no encontrada");
                response.setSuccess(false);
                return response;
            }
            materiaRepository.deleteById(id);
            response.setMessage("Materia eliminada exitosamente");
            response.setSuccess(true);
            return response;
        } catch (Exception e) {
            GenericResponseDto response = new GenericResponseDto();
            response.setMessage("Error al eliminar la materia: " + e.getMessage());
            response.setSuccess(false);
            return response;
        }
    }

    // obtener lista de materias por usuario
    public List<MateriaResponseDto> obtenerMateriasPorUsuario(Integer usuarioId) {
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
                        return new MateriaResponseDto(m, horarios);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    // Agregar uno o varios horarios a una materia existente
    public GenericResponseDto agregarHorariosAMateria(Integer materiaId,
            List<HorarioMateriaRequestDto> horariosRequest) {
        try {
            GenericResponseDto response = new GenericResponseDto();
            Optional<Materia> materiaOpt = materiaRepository.findById(materiaId);
            if (materiaOpt.isEmpty()) {
                response.setMessage("Materia no encontrada");
                response.setSuccess(false);
                return response;
            }
            Materia materia = materiaOpt.get();
            if (horariosRequest == null || horariosRequest.isEmpty()) {
                response.setMessage("No se recibieron horarios para agregar");
                response.setSuccess(false);
                return response;
            }
            for (HorarioMateriaRequestDto req : horariosRequest) {
                HorarioPorMateria horario = new HorarioPorMateria();
                horario.setMateria(materia);
                horario.setDia(DiaSemana.valueOf(req.getDia().toUpperCase()));
                horario.setHoraInicio(java.time.LocalTime.parse(req.getHoraInicio()));
                horario.setHoraFin(java.time.LocalTime.parse(req.getHoraFin()));
                horarioRepository.save(horario);
            }
            response.setMessage("Horarios agregados exitosamente");
            response.setSuccess(true);
            return response;
        } catch (Exception e) {
            GenericResponseDto response = new GenericResponseDto();
            response.setMessage("Error al agregar horarios: " + e.getMessage());
            response.setSuccess(false);
            return response;
        }
    }

    // lista de materias con horario, para el planificador (si una materia tiene
    // varios horarios, se repite la materia en cada horario)
    public List<MateriaPlannerResponseDto> obtenerMateriasConHorariosPorUsuario(Integer usuarioId) {
        try {
            if (usuarioId == null) {
                return List.of();
            }
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return List.of();
            }
            List<Materia> materias = materiaRepository.findByUsuarioId(usuarioId);
            // Solo materias en estado CURSANDO y con al menos un horario
            return materias.stream()
                    .filter(m -> m.getEstado() != null && m.getEstado().name().equalsIgnoreCase("CURSANDO"))
                    .flatMap(m -> {
                        List<HorarioPorMateria> horarios = horarioRepository.findByMateriaId(m.getId());
                        if (horarios == null || horarios.isEmpty()) {
                            return Stream.empty();
                        }
                        return horarios.stream().map(horario -> {
                            MateriaPlannerResponseDto dto = new MateriaPlannerResponseDto();
                            dto.setTitulo(m.getTitulo());
                            dto.setColor(m.getColor());
                            dto.setHoraInicio(horario.getHoraInicio());
                            dto.setHoraFin(horario.getHoraFin());
                            dto.setDia(horario.getDia());
                            return dto;
                        });
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
}
