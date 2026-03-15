package aktech.planificador.modules.subject.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import aktech.planificador.shared.event.CareerDeletedEvent;

@Component
public class CareerEventListener {
    private static final Logger logger = LoggerFactory.getLogger(CareerEventListener.class);

    @EventListener
    public void onCareerDeleted(CareerDeletedEvent event) {
        // Base para cleanup en Subject cuando se implemente el repositorio modular.
        logger.info("CareerDeletedEvent recibido. careerId={} userId={}", event.getCareerId(), event.getUserId());
    }
}
