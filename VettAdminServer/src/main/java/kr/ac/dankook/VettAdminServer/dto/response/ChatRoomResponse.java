package kr.ac.dankook.VettAdminServer.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {

    private String roomId;
    private int memberCount;
    private LocalDateTime createdDateTime;
    private String title;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
