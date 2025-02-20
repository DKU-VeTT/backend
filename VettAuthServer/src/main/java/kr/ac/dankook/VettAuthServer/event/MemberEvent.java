package kr.ac.dankook.VettAuthServer.event;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberEvent {

    // User PK
    private String id;
    private String eventId;
    private String userId;
    private String name;
    private String email;
}
