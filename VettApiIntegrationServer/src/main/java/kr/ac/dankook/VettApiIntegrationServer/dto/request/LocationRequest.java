package kr.ac.dankook.VettApiIntegrationServer.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@Getter
@Data
@NoArgsConstructor
@Builder
public class LocationRequest {
    @NotNull(message = "Start Latitude is Required.")
    private double startLatitude;
    @NotNull(message = "Start Longitude is Required.")
    private double startLongitude;
    @NotNull(message = "End Latitude is Required.")
    private double endLatitude;
    @NotNull(message = "End Longitude is Required.")
    private double endLongitude;
}
