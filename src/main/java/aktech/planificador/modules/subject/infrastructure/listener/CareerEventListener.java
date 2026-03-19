package aktech.planificador.modules.subject.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import aktech.planificador.modules.subject.persistence.SubjectRepository;
import aktech.planificador.shared.event.CareerDeletedEvent;

@Component
public class CareerEventListener {
    private static final Logger logger = LoggerFactory.getLogger(CareerEventListener.class);

    private final SubjectRepository subjectRepository;

    public CareerEventListener(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @EventListener
    @Transactional
    public void onCareerDeleted(CareerDeletedEvent event) {
        long deletedSubjects = subjectRepository.deleteByCareerId(event.getCareerId());
        logger.info("CareerDeletedEvent procesado en Subject. careerId={} userId={} subjectsDeleted={}",
                event.getCareerId(),
                event.getUserId(),
                deletedSubjects);
    }
}
