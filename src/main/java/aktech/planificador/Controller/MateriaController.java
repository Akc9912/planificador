package aktech.planificador.Controller;

import aktech.planificador.Dto.GenericResponseDto;
import aktech.planificador.Dto.materia.HorarioMateriaRequestDto;
import aktech.planificador.Dto.materia.MateriaPlannerResponseDto;
import aktech.planificador.Dto.materia.MateriaRequestDto;
import aktech.planificador.Dto.materia.MateriaResponseDto;
import aktech.planificador.Service.core.MateriaService;

import java.util.List;

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
    public GenericResponseDto modificarMateria(@PathVariable("id") Integer idMateria,
            @RequestBody MateriaRequestDto request) {
        return materiaService.modificarMateria(idMateria, request);
    }

    // Eliminar materia
    @DeleteMapping("/eliminar/{id}")
    public GenericResponseDto eliminarMateria(@PathVariable Integer idMateria, @RequestParam Integer usuarioId) {
        return materiaService.eliminarMateria(idMateria, usuarioId);
    }

    // Obtener materias por usuario
    @GetMapping("/usuario/{usuarioId}")
    public java.util.List<MateriaResponseDto> obtenerMateriasPorUsuario(@PathVariable Integer usuarioId) {
        return materiaService.obtenerMateriasPorUsuario(usuarioId);
    }

    // Obtener materias con horarios por usuario - para vista planner
    @GetMapping("/usuario/{usuarioId}/con-horarios")
    public List<MateriaPlannerResponseDto> obtenerMateriasConHorariosPorUsuario(@PathVariable Integer usuarioId) {
        return materiaService.obtenerMateriasConHorariosPorUsuario(usuarioId);
    }

}
