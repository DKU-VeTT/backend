package kr.ac.dankook.VettApiIntegrationServer.service;

import kr.ac.dankook.VettApiIntegrationServer.dto.request.CoordinateRequest;
import kr.ac.dankook.VettApiIntegrationServer.dto.request.LocationRequest;
import kr.ac.dankook.VettApiIntegrationServer.dto.response.CoordinateResponse;
import kr.ac.dankook.VettApiIntegrationServer.dto.response.RouteResponse;
import kr.ac.dankook.VettApiIntegrationServer.exception.ApiErrorCode;
import kr.ac.dankook.VettApiIntegrationServer.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class LocationService {

    @Value("${api.kakao.app-key}")
    private String KAKAO_APP_KEY;
    @Value("${api.kakao.address-to-coordinate-host}")
    private String KAKAO_ADDRESS_TO_COORDINATE_HOST;
    @Value("${api.kakao.address-to-coordinate-path}")
    private String KAKAO_ADDRESS_TO_COORDINATE_PATH;
    @Value("${api.kakao.route-host}")
    private String KAKAO_ROUTE_HOST;
    @Value("${api.kakao.route-path}")
    private String KAKAO_ROUTE_PATH;

    public RouteResponse getRouteInformationProcess(LocationRequest locationRequest){

        String apiKey = "KakaoAK " + KAKAO_APP_KEY;
        RestClient restClient = RestClient.create();

        CoordinateRequest startLocation = new CoordinateRequest(locationRequest.getStartLatitude(), locationRequest.getStartLongitude());
        CoordinateRequest endLocation = new CoordinateRequest(locationRequest.getEndLatitude(), locationRequest.getEndLongitude());

        String responseBody = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(KAKAO_ROUTE_HOST)
                        .path(KAKAO_ROUTE_PATH)
                        .queryParam("origin",startLocation.getLongitude()+","+ startLocation.getLatitude())
                        .queryParam("destination",endLocation.getLongitude()+","+ endLocation.getLatitude())
                        .queryParam("waypoints","")
                        .build())
                .header("Authorization", apiKey)
                .header("Content-Type","application/json")
                .retrieve()
                .body(String.class);
        try{
            JSONObject json = new JSONObject(responseBody);
            JSONArray routes = json.getJSONArray("routes");
            JSONObject routeObject = (JSONObject) routes.get(0);
            JSONObject summaryObject = routeObject.getJSONObject("summary");

            String distance;
            int distanceObject = summaryObject.getInt("distance");
            if (distanceObject >= 1000){
                distanceObject = Math.round(distanceObject / 1000.f);
                distance = distanceObject+"km";
            }else {
                distance = distanceObject + "m";
            }
            int duration = Math.round(summaryObject.getInt("duration") / 60.0f);
            int taxiFare = summaryObject.getJSONObject("fare").getInt("taxi");
            int tollFare = summaryObject.getJSONObject("fare").getInt("toll");
            return new RouteResponse(
                    startLocation,endLocation,distance,duration+"분",taxiFare+"원",tollFare+"원");
        }catch(JSONException e){
            log.info(e.toString());
            log.info("Route Process Error. Return Default Message - {}",e.toString());
            return new RouteResponse(
                    startLocation,endLocation,"교통정보 없음.","교통정보 없음.","교통정보 없음.","교통정보 없음.");
        }
    }

    public CoordinateResponse getCoordinateByAddressProcess(String address){

        String apiKey = "KakaoAK " + KAKAO_APP_KEY;
        RestClient restClient = RestClient.create();

        String responseBody = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(KAKAO_ADDRESS_TO_COORDINATE_HOST)
                        .path(KAKAO_ADDRESS_TO_COORDINATE_PATH)
                        .queryParam("query", address)
                        .build())
                .header("Authorization", apiKey)
                .retrieve()
                .body(String.class);
        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray documents = json.getJSONArray("documents");
            double longitude = documents.getJSONObject(0).getDouble("x");
            double latitude = documents.getJSONObject(0).getDouble("y");
            return new CoordinateResponse(latitude, longitude);
        } catch (JSONException e) {
            log.info("Coordinate Process Error");
            throw new ApiException(ApiErrorCode.JSON_PARSING_ERROR);
        }
    }
}
