package kr.ac.dankook.VettPlaceServer.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "VeTT_Place")
@Builder
@Getter
@Setter
public class Place {

    @Id
    private String id;
    private String addFeeInfo;
    private String address;
    private String category;
    private String feeInfo;
    private String holidayInfo;
    private String isInside;
    private String isOutside;
    private String isParking;
    private double latitude;
    private double longitude;
    private String maxSizeInfo;
    private String operatingInfo;
    private String phoneNumber;
    private String placeName;
    private String restrictionInfo;
    private String regionCode;
}
