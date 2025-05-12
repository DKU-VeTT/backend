package kr.ac.dankook.VettPlaceServer.dto.response;

import kr.ac.dankook.VettPlaceServer.document.Place;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PlaceDistResponse {

    private Place place;
    private double distance;
    private String distanceStringFormat;
}
