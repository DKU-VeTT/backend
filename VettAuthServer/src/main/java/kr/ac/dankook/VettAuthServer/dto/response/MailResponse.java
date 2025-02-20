package kr.ac.dankook.VettAuthServer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MailResponse {
    private String mailAddress;
    private String title;
    private String content;
}
