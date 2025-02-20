package kr.ac.dankook.VettAuthServer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest {
    @NotBlank(message = "RefreshToken is required.")
    private String refreshToken;
}
