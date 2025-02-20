package kr.ac.dankook.VettAuthServer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindIdRequest {

    @NotBlank(message = "Name is Required.")
    @Size(min=2,max=50,message="Name must be between 2 and 50 characters.")
    private String name;
    @NotBlank(message = "Email is required.")
    @Email(message = "Email format is invalid.")
    private String email;
}
