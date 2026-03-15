package aktech.planificador.Controller;

import java.util.Map;

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

import aktech.planificador.Dto.evento.EventoRequestDto;

@RestController
@RequestMapping("/eventos")
public class EventoController {
    private static final String MESSAGE = "Modulo Eventos desacoplado del MVP actual";

    private ResponseEntity<Map<String, String>> endpointDisabled() {
        return ResponseEntity.status(HttpStatus.GONE).body(Map.of("message", MESSAGE));
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, String>> crearEvento(@RequestBody EventoRequestDto request) {
        return endpointDisabled();
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<Map<String, String>> modificarEvento(@PathVariable Integer id,
            @RequestBody EventoRequestDto request) {
        return endpointDisabled();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, String>> eliminarEvento(@PathVariable Integer id) {
        return endpointDisabled();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Map<String, String>> obtenerEventosPorUsuario(@PathVariable Integer usuarioId) {
        return endpointDisabled();
    }

    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<Map<String, String>> obtenerEventosPorMateria(@PathVariable Integer materiaId) {
        return endpointDisabled();
    }
}
