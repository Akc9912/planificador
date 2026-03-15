package aktech.planificador.Controller;

import java.util.Map;

import aktech.planificador.Dto.recordatorio.RecordatorioRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recordatorios")
public class RecordatorioController {
    private static final String MESSAGE = "Modulo Recordatorios desacoplado del MVP actual";

    private ResponseEntity<Map<String, String>> endpointDisabled() {
        return ResponseEntity.status(HttpStatus.GONE).body(Map.of("message", MESSAGE));
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, String>> crearRecordatorio(@RequestBody RecordatorioRequestDto request) {
        return endpointDisabled();
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<Map<String, String>> modificarRecordatorio(@PathVariable Integer id,
            @RequestBody RecordatorioRequestDto request) {
        return endpointDisabled();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, String>> eliminarRecordatorio(@PathVariable Integer id) {
        return endpointDisabled();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Map<String, String>> obtenerRecordatoriosPorUsuario(@PathVariable Integer usuarioId) {
        return endpointDisabled();
    }

    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<Map<String, String>> obtenerRecordatoriosPorMateria(@PathVariable Integer materiaId) {
        return endpointDisabled();
    }
}
