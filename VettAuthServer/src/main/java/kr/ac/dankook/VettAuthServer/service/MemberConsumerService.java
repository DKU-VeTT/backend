package kr.ac.dankook.VettAuthServer.service;

import kr.ac.dankook.VettAuthServer.entity.OutboxStatus;
import kr.ac.dankook.VettAuthServer.event.EventObjectMapper;
import kr.ac.dankook.VettAuthServer.event.MemberEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberConsumerService {


    private final EventObjectMapper eventObjectMapper;
    private final MemberOutboxService memberOutboxService;

    @KafkaListener(groupId = "NaHC_MEMBER", topics = "USER_MODIFIED")
    public void consumeFromUserModified(String message, Acknowledgment acknowledgment){

        log.info("User Modified consumed message: {}",message);
        MemberEvent event = eventObjectMapper.jsonToEventObject(message);
        memberOutboxService.convertOutboxStatus(event.getEventId(), OutboxStatus.MESSAGE_CONSUME);
        acknowledgment.acknowledge();
    }

    @KafkaListener(groupId = "NaHC_MEMBER", topics = "USER_CREATED")
    public void consumeFromUserCreated(String message, Acknowledgment acknowledgment){

        log.info("User Created consumed message: {}",message);
        MemberEvent event = eventObjectMapper.jsonToEventObject(message);
        memberOutboxService.convertOutboxStatus(event.getEventId(), OutboxStatus.MESSAGE_CONSUME);
        acknowledgment.acknowledge();
    }
    @KafkaListener(groupId = "NaHC_MEMBER", topics = "USER_DELETED")
    public void consumeFromUserDeleted(String message, Acknowledgment acknowledgment){

        log.info("User Deleted consumed message: {}",message);
        MemberEvent event = eventObjectMapper.jsonToEventObject(message);
        memberOutboxService.convertOutboxStatus(event.getEventId(), OutboxStatus.MESSAGE_CONSUME);
        acknowledgment.acknowledge();
    }
}
