package kr.ac.dankook.VettAuthServer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Outbox{

    // UUID Random PK
    @Id
    private String id;

    // Domain : "User"
    @Column(nullable = false)
    private String aggregateType;

    // Type : "UserModified"
    @Column(nullable = false)
    private String eventType;

    // Payload : "{ key(User PK) : "", eventId : "", userId : "", name : "", email : "" }"
    @Lob
    @Column(nullable = false)
    private String payload;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

}