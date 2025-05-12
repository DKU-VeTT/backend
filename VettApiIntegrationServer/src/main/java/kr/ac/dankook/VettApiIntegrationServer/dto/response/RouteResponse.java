package kr.ac.dankook.VettApiIntegrationServer.dto.response;

import kr.ac.dankook.VettApiIntegrationServer.dto.request.CoordinateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteResponse {
    private CoordinateRequest startLocation;
    private CoordinateRequest endLocation;
    private String distance;
    private String time;
    private String taxiFare;
    private String tollFare;
}
