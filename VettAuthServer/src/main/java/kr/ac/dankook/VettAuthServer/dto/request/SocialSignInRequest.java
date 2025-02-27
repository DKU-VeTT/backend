package kr.ac.dankook.VettAuthServer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialSignInRequest {

    @NotBlank(message = "Name is Required.")
    private String name;
    @NotBlank(message = "Email is Required.")
    @Email(message = "Email format is invalid.")
    private String email;
    @NotBlank(message = "User ID is Required.")
    private String userId;

}
