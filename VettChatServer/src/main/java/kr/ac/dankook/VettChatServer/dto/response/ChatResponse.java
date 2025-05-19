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
public class ChatResponse {

    private String content;
    private String name;
    private String memberId;
    private LocalDateTime time;
}
