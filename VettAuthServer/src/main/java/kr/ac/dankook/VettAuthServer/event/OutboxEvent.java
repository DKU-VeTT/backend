package kr.ac.dankook.VettAuthServer.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class OutboxEvent {

    private String id;
    private String aggregateType;
    private String eventType;
    private String payload;
}
