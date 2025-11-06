package aktech.planificador.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import aktech.planificador.Dto.recordatorio.RecordatorioRequestDto;
import aktech.planificador.Dto.recordatorio.RecordatorioResponseDto;
import aktech.planificador.Service.core.RecordatorioService;

@RestController
@RequestMapping("/recordatorios")
public class RecordatorioController {
    private final RecordatorioService recordatorioService;

    public RecordatorioController(RecordatorioService recordatorioService) {
        this.recordatorioService = recordatorioService;
    }

    @PostMapping("/crear")
    public RecordatorioResponseDto crearRecordatorio(@RequestBody RecordatorioRequestDto request) {
        return recordatorioService.crearRecordatorio(request);
    }

    @PutMapping("/modificar/{id}")
    public RecordatorioResponseDto modificarRecordatorio(@PathVariable Integer id,
            @RequestBody RecordatorioRequestDto request) {
        return recordatorioService.modificarRecordatorio(id, request);
    }

    @DeleteMapping("/eliminar/{id}")
    public void eliminarRecordatorio(@PathVariable Integer id, @RequestParam Integer usuarioId) {
        recordatorioService.eliminarRecordatorio(id, usuarioId);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<RecordatorioResponseDto> obtenerRecordatoriosPorUsuario(@PathVariable Integer usuarioId) {
        return recordatorioService.obtenerRecordatoriosPorUsuario(usuarioId);
    }

    @GetMapping("/materia/{materiaId}")
    public List<RecordatorioResponseDto> obtenerRecordatoriosPorMateria(@PathVariable Integer materiaId) {
        return recordatorioService.obtenerRecordatoriosPorMateria(materiaId);
    }
}
