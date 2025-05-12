package kr.ac.dankook.VettPlaceServer.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterRequest {

    private String placeName;
    @NotBlank(message = "Category is Required.")
    @Pattern(regexp = "동물병원|반려동물용품|여행지|미용|동물약국",
            message = "Category must be one of: 동물병원, 반려동물용품, 여행지, 미용, 동물약국")
    private String category;
    private String regionCode;

    @JsonProperty("isParking")
    private boolean isParking;
    @JsonProperty("isInside")
    private boolean isInside;
    @JsonProperty("isOutside")
    private boolean isOutside;
}
