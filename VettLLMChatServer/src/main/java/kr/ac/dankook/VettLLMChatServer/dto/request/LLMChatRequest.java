package kr.ac.dankook.VettLLMChatServer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class LLMChatRequest {
    private String question;
    private String answer;
}
