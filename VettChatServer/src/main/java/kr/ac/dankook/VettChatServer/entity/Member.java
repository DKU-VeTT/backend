package kr.ac.dankook.VettChatServer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    private String id;
    private String userId; // User Id
    private String email; // User Email
    private String name; // User name
}