package kr.ac.dankook.VettApiIntegrationServer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {

    @NotBlank(message = "Address is Required.")
    @Size(min=2 ,message="Address must be at least 2 characters long.")
    private String address;
}