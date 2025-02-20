package kr.ac.dankook.VettAuthServer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "Name is Required.")
    @Size(min=2,max=50,message="Name must be between 2 and 50 characters.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email format is invalid.")
    private String email;

    @NotBlank(message = "ID is required.")
    @Size(min = 7, max = 30, message = "ID must be between 7 and 30 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "ID can only contain alphanumeric characters.")
    private String userId;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    @Pattern(
        regexp = "^(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$",
        message = "Password must include at least one number and one special character."
    )
    private String password;

}
