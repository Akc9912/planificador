package aktech.planificador.Controller;

import aktech.planificador.DTO.materia.MateriaRequestDto;
import aktech.planificador.DTO.materia.MateriaResponseDto;
import aktech.planificador.Service.core.MateriaService;
import aktech.planificador.DTO.GenericResponseDto;
import aktech.planificador.DTO.materia.HorarioMateriaRequestDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/materia")
public class MateriaController {
    private final MateriaService materiaService;

    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    // Crear materia
    @PostMapping("/crear")
    public GenericResponseDto crearMateria(@RequestBody MateriaRequestDto request) {
        return materiaService.crearMateria(request);
    }

    // Modificar materia
    @PutMapping("/modificar/{id}")
    public GenericResponseDto modificarMateria(@PathVariable Integer id, @RequestBody MateriaRequestDto request) {
        return materiaService.modificarMateria(id, request);
    }

    // Eliminar materia
    @DeleteMapping("/eliminar/{id}")
    public GenericResponseDto eliminarMateria(@PathVariable Integer id) {
        return materiaService.eliminarMateria(id);
    }

    // Obtener materias por usuario
    @GetMapping("/usuario/{usuarioId}")
    public java.util.List<MateriaResponseDto> obtenerMateriasPorUsuario(@PathVariable Integer usuarioId) {
        return materiaService.obtenerMateriasPorUsuario(usuarioId);
    }

    // Obtener materias con horarios por usuario
    @GetMapping("/usuario/{usuarioId}/con-horarios")
    public java.util.List<MateriaResponseDto> obtenerMateriasConHorariosPorUsuario(@PathVariable Integer usuarioId) {
        return materiaService.obtenerMateriasConHorariosPorUsuario(usuarioId);
    }

    // Agregar horarios a una materia
    @PostMapping("/{materiaId}/horarios")
    public GenericResponseDto agregarHorariosAMateria(@PathVariable Integer materiaId,
            @RequestBody java.util.List<HorarioMateriaRequestDto> horariosRequest) {
        return materiaService.agregarHorariosAMateria(materiaId, horariosRequest);
    }
}
