package kr.ac.dankook.VettApiIntegrationServer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CoordinateResponse {
    private double latitude;
    private double longitude;
}
