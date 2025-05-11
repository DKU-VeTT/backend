package kr.ac.dankook.VettChatServer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {

    private String content;
    private String roomId;
    private String memberId;
}

