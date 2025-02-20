package kr.ac.dankook.VettAuthServer.service;

import kr.ac.dankook.VettAuthServer.entity.Outbox;
import kr.ac.dankook.VettAuthServer.entity.OutboxStatus;
import kr.ac.dankook.VettAuthServer.event.OutboxEvent;
import kr.ac.dankook.VettAuthServer.kafka.KafkaTopic;
import kr.ac.dankook.VettAuthServer.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberOutboxService {

    private final OutboxRepository outboxRepository;

    public String getMemberKafkaTopic(String eventType){
        return switch (eventType) {
            case "UserModified" -> KafkaTopic.USER_MODIFIED.toString();
            case "UserCreated" -> KafkaTopic.USER_CREATED.toString();
            case "UserDeleted" -> KafkaTopic.USER_DELETED.toString();
            default -> null;
        };
    }

    public List<Outbox> getFailedOutboxEvents(){
        return outboxRepository.findByStatus(OutboxStatus.PERMANENTLY_FAILED.toString());
    }
    @Transactional
    public void saveNewOutboxProcess(OutboxEvent outboxEvent) {

        Outbox outbox = Outbox.builder()
                .id(outboxEvent.getId())
                .aggregateType(outboxEvent.getAggregateType())
                .eventType(outboxEvent.getEventType())
                .payload(outboxEvent.getPayload())
                .timestamp(LocalDateTime.now())
                .status(OutboxStatus.READY_TO_PUBLISH)
                .build();
        outboxRepository.save(outbox);
    }

    @Transactional
    public void convertOutboxStatus(String eventId,OutboxStatus status){
        Optional<Outbox> targetBox = outboxRepository.findById(eventId);
        if (targetBox.isPresent()) {
            Outbox outbox = targetBox.get();
            switch (status){
                case READY_TO_PUBLISH:
                    outbox.setStatus(OutboxStatus.READY_TO_PUBLISH);
                    break;
                case PUBLISHED:
                    outbox.setStatus(OutboxStatus.PUBLISHED);
                    break;
                case FAILED:
                    outbox.setStatus(OutboxStatus.FAILED);
                    break;
                case MESSAGE_CONSUME:
                    outbox.setStatus(OutboxStatus.MESSAGE_CONSUME);
                    break;
            }
            outboxRepository.save(outbox);
        }
    }
}
