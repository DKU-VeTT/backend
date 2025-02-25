package kr.ac.dankook.VettAdminServer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Old Password is required.")
    private String oldPassword;

    @NotBlank(message = "New Password is required.")
    @Size(min = 8, message = "New Password must be at least 8 characters long.")
    @Pattern(
            regexp = "^(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$",
            message = "New Password must include at least one number and one special character."
    )
    private String newPassword;
}
