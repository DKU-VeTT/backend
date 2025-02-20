package kr.ac.dankook.VettAuthServer.kafka;

import kr.ac.dankook.VettAuthServer.entity.Outbox;
import kr.ac.dankook.VettAuthServer.entity.OutboxStatus;
import kr.ac.dankook.VettAuthServer.event.OutboxEvent;
import kr.ac.dankook.VettAuthServer.exception.ApiErrorCode;
import kr.ac.dankook.VettAuthServer.exception.KafkaSendException;
import kr.ac.dankook.VettAuthServer.service.MemberOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final MemberOutboxService memberOutboxService;
    private final KafkaTemplate<String,String> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOutboxEvent(OutboxEvent event) {
        memberOutboxService.saveNewOutboxProcess(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            retryFor = { KafkaSendException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000),
            recover = "recover")

    public void handleKafkaEvent(OutboxEvent event) {

        String eventId = event.getId();
        String payload = event.getPayload();
        try{
            String topic = memberOutboxService.getMemberKafkaTopic(event.getEventType());
            kafkaTemplate.send(topic,payload);
            memberOutboxService.convertOutboxStatus(eventId, OutboxStatus.PUBLISHED);
        }catch (Exception e){
            log.error("Failed to send Kafka payload-{}, eventId-{}",payload,eventId,e);
            memberOutboxService.convertOutboxStatus(eventId, OutboxStatus.FAILED);
            throw new KafkaSendException(ApiErrorCode.KAFKA_SEND_EXCEPTION);
        }
    }

    @Recover
    public void recover(KafkaSendException e, OutboxEvent event) {
        log.error("Kafka message retry failed. Marking event as permanently failed, eventId: {}", event.getId(), e);
        memberOutboxService.convertOutboxStatus(event.getId(), OutboxStatus.PERMANENTLY_FAILED);
    }

    @Scheduled(fixedDelay = 60000 * 3) // 3 minute
    public void retryFailedMessages() {
        List<Outbox> failedEvents = memberOutboxService.getFailedOutboxEvents();
        for (Outbox outbox : failedEvents) {
            String topic = memberOutboxService.getMemberKafkaTopic(outbox.getEventType());
            String payload = outbox.getPayload();
            try {
                kafkaTemplate.send(topic,payload);
                log.info("Successfully retried Kafka message, eventId: {}", outbox.getId());
            } catch (Exception e) {
                log.error("Failed to retry Kafka message, eventId: {}", outbox.getId(), e);
            }
        }
    }
}
