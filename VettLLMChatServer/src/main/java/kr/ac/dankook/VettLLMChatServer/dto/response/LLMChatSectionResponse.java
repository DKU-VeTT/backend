package kr.ac.dankook.VettLLMChatServer.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class LLMChatSectionResponse {

    private String id;
    private LocalDateTime time;
    private String title;
}
