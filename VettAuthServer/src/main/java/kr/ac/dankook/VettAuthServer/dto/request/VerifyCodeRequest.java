package kr.ac.dankook.VettAuthServer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCodeRequest {

    @NotBlank(message = "Auth Mail Token is required.")
    private String authMailToken;
    @NotBlank(message = "VerifyCode is required.")
    private String verifyCode;
}