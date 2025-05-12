package kr.ac.dankook.VettPlaceServer.dto.response;

import kr.ac.dankook.VettPlaceServer.document.Place;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceResponse {
    private List<Place> places;
    private int count;
}
