package aktech.planificador.Controller;

import aktech.planificador.DTO.materia.MateriaRequestDTO;
import aktech.planificador.DTO.materia.MateriaResponseDTO;
import aktech.planificador.Service.core.MateriaService;
import aktech.planificador.DTO.GenericResponseDTO;
import aktech.planificador.DTO.materia.HorarioMateriaRequest;
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
    public GenericResponseDTO crearMateria(@RequestBody MateriaRequestDTO request) {
        return materiaService.crearMateria(request);
    }

    // Modificar materia
    @PutMapping("/modificar/{id}")
    public GenericResponseDTO modificarMateria(@PathVariable Integer id, @RequestBody MateriaRequestDTO request) {
        return materiaService.modificarMateria(id, request);
    }

    // Eliminar materia
    @DeleteMapping("/eliminar/{id}")
    public GenericResponseDTO eliminarMateria(@PathVariable Integer id) {
        return materiaService.eliminarMateria(id);
    }

    // Obtener materias por usuario
    @GetMapping("/usuario/{usuarioId}")
    public java.util.List<MateriaResponseDTO> obtenerMateriasPorUsuario(@PathVariable Integer usuarioId) {
        return materiaService.obtenerMateriasPorUsuario(usuarioId);
    }

    // Obtener materias con horarios por usuario
    @GetMapping("/usuario/{usuarioId}/con-horarios")
    public java.util.List<MateriaResponseDTO> obtenerMateriasConHorariosPorUsuario(@PathVariable Integer usuarioId) {
        return materiaService.obtenerMateriasConHorariosPorUsuario(usuarioId);
    }

    // Agregar horarios a una materia
    @PostMapping("/{materiaId}/horarios")
    public GenericResponseDTO agregarHorariosAMateria(@PathVariable Integer materiaId,
            @RequestBody java.util.List<HorarioMateriaRequest> horariosRequest) {
        return materiaService.agregarHorariosAMateria(materiaId, horariosRequest);
    }
}
