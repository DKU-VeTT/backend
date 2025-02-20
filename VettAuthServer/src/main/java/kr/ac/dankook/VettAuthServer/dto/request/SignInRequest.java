package kr.ac.dankook.VettAuthServer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {

    @NotBlank(message = "ID is Required.")
    private String userId;
    @NotBlank(message = "Password is Required.")
    private String password;
}
