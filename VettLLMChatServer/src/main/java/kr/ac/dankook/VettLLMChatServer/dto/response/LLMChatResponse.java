package kr.ac.dankook.VettLLMChatServer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Setter
@Getter
public class LLMChatResponse {

    private String chatSectionId;
    private String chatId;
    private String question;
    private String answer;
    private LocalDateTime time;
}
