package kr.ac.dankook.VettPlaceServer.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoordinateRequest {

    @NotNull(message = "Latitude is Required.")
    @Min(value = -90, message = "Latitude must be greater than or equal to -90.")
    @Max(value = 90, message = "Latitude must be less than or equal to 90.")
    private Double latitude;

    @NotNull(message = "Longitude is Required.")
    @Min(value = -180, message = "Longitude must be greater than or equal to -180.")
    @Max(value = 180, message = "Longitude must be less than or equal to 180.")
    private Double longitude;
}

