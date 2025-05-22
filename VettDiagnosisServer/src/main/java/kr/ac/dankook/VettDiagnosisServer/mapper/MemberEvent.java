package kr.ac.dankook.VettDiagnosisServer.mapper;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberEvent {

    private String id;
    private String eventId;
    private String userId; // User Id
    private String email; // User Email
    private String name; // User name
}
