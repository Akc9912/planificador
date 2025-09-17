package aktech.planificador.Service.core;

import org.springframework.stereotype.Service;
import aktech.planificador.Repository.core.RecordatorioRepository;

@Service
public class RecordatorioService {
    private final RecordatorioRepository recordatorioRepository;

    public RecordatorioService(RecordatorioRepository recordatorioRepository) {
        this.recordatorioRepository = recordatorioRepository;
    }
}
