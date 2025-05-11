package kr.ac.dankook.VettChatServer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponse {

    private String roomId;
    private int memberCount;
    private LocalDateTime createdDateTime;
    private String title;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
