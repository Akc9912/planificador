package aktech.planificador.Service.core;

import aktech.planificador.Model.enums.DiaSemana;
import aktech.planificador.Model.historial.HorarioPorMateria;
import aktech.planificador.Repository.historial.HorarioPorMateriaRepository;
import aktech.planificador.Dto.GenericResponseDto;
import aktech.planificador.Dto.materia.DashboardDataDto;
import aktech.planificador.Dto.materia.HorarioMateriaRequestDto;
import aktech.planificador.Dto.materia.MateriaRequestDto;
import aktech.planificador.Dto.materia.MateriaResponseDto;
import aktech.planificador.Dto.materia.MateriaPlannerResponseDto;
import aktech.planificador.Dto.materia.HorarioMateriaResponseDto;
import aktech.planificador.Model.core.Materia;
import aktech.planificador.Model.core.Usuario;
import aktech.planificador.Model.enums.EstadoMateria;
import aktech.planificador.Repository.core.MateriaRepository;
import aktech.planificador.Repository.core.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
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

    @Transactional
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

    @Transactional
    public GenericResponseDto modificarMateria(Integer idMateria, MateriaRequestDto request) {
        try {
            GenericResponseDto response = new GenericResponseDto();
            Optional<Materia> materiaOpt = materiaRepository.findById(idMateria);
            if (materiaOpt.isEmpty()) {
                response.setMessage("Materia no encontrada");
                response.setSuccess(false);
                return response;
            }
            Materia materia = materiaOpt.get();

            // Verificar que el usuarioId del request sea el dueño de la materia
            if (request.getUsuarioId() == null || materia.getUsuario() == null
                    || !materia.getUsuario().getId().equals(request.getUsuarioId())) {
                response.setMessage("No tienes permisos para modificar esta materia");
                response.setSuccess(false);
                return response;
            }

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

            // Actualizar horarios: eliminar los existentes y agregar los nuevos del request
            List<HorarioPorMateria> horariosActuales = horarioRepository.findByMateriaId(materia.getId());
            for (HorarioPorMateria h : horariosActuales) {
                horarioRepository.delete(h);
            }
            if (request.getHorarios() != null && !request.getHorarios().isEmpty()) {
                for (HorarioMateriaRequestDto horarioReq : request.getHorarios()) {
                    HorarioPorMateria horario = new HorarioPorMateria();
                    horario.setMateria(materia);
                    horario.setDia(
                            aktech.planificador.Model.enums.DiaSemana.valueOf(horarioReq.getDia().toUpperCase()));
                    horario.setHoraInicio(java.time.LocalTime.parse(horarioReq.getHoraInicio()));
                    horario.setHoraFin(java.time.LocalTime.parse(horarioReq.getHoraFin()));
                    horarioRepository.save(horario);
                }
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

    @Transactional
    public GenericResponseDto eliminarMateria(Integer idMateria, Integer usuarioId) {
        try {
            GenericResponseDto response = new GenericResponseDto();
            Optional<Materia> materiaOpt = materiaRepository.findById(idMateria);
            if (materiaOpt.isEmpty()) {
                response.setMessage("Materia no encontrada");
                response.setSuccess(false);
                return response;
            }
            Materia materia = materiaOpt.get();

            // Verificar que el usuarioId sea el dueño de la materia
            if (usuarioId == null || materia.getUsuario() == null || !materia.getUsuario().getId().equals(usuarioId)) {
                response.setMessage("No tienes permisos para eliminar esta materia");
                response.setSuccess(false);
                return response;
            }
            // Eliminar horarios asociados
            List<HorarioPorMateria> horarios = horarioRepository.findByMateriaId(materia.getId());
            for (HorarioPorMateria h : horarios) {
                horarioRepository.delete(h);
            }
            // Delete permanente
            materiaRepository.deleteById(idMateria);
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
                        List<HorarioMateriaResponseDto> horariosDto = horarios.stream().map(h -> {
                            HorarioMateriaResponseDto dto = new HorarioMateriaResponseDto();
                            dto.setDia(h.getDia().toString());
                            dto.setHoraInicio(h.getHoraInicio() != null ? h.getHoraInicio().toString() : null);
                            dto.setHoraFin(h.getHoraFin() != null ? h.getHoraFin().toString() : null);
                            return dto;
                        }).collect(Collectors.toList());
                        MateriaResponseDto materiaDto = new MateriaResponseDto();
                        materiaDto.setId(m.getId());
                        materiaDto.setTitulo(m.getTitulo());
                        materiaDto.setEstado(m.getEstado() != null ? m.getEstado().toString() : null);
                        materiaDto.setColor(m.getColor());
                        materiaDto.setPromocionable(m.getPromocionable() != null ? m.getPromocionable() : false);
                        materiaDto.setNotaPromocion(m.getNotaPromocion());
                        materiaDto.setCalificacion(m.getCalificacion());
                        materiaDto.setHorarios(horariosDto);
                        return materiaDto;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
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