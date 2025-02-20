package kr.ac.dankook.VettAuthServer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthMailResponse {

    private String email;
    private String userId;
    private boolean isSend;
}
