package kr.ac.dankook.VettPlaceServer.service;

import kr.ac.dankook.VettPlaceServer.document.Place;
import kr.ac.dankook.VettPlaceServer.dto.request.CoordinateRequest;
import kr.ac.dankook.VettPlaceServer.dto.request.FilterRequest;
import kr.ac.dankook.VettPlaceServer.dto.request.LocationRequest;
import kr.ac.dankook.VettPlaceServer.dto.response.PlaceDistResponse;
import kr.ac.dankook.VettPlaceServer.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public List<Place> findAllPlaceProcess(){
        return placeRepository.findAll();
    }

    public List<Place> findPlaceByCategoryProcess(String category){
        return placeRepository.findByCategory(category);
    }

    public List<String> findPlaceCategory(){
        return placeRepository.findDistinctCategories();
    }

    public List<Place> findByKeywordProcess(String keyword){
        return placeRepository.findByPlaceNameContainingIgnoreCase(keyword);
    }

    public List<Place> findPlaceByFilter(FilterRequest filterRequest){
        List<Place> filteredList;
        if (filterRequest.getPlaceName() != null && !filterRequest.getPlaceName().isEmpty()){
            filteredList = placeRepository.findByCategoryAndPlaceNameContaining(
                    filterRequest.getCategory(),filterRequest.getPlaceName()
            );
        }else{
            filteredList = findPlaceByCategoryProcess(filterRequest.getCategory());
        }
        if (filterRequest.getRegionCode() != null && !filterRequest.getRegionCode().isEmpty()){
            filteredList =  filteredList.stream()
                    .filter(place -> place.getRegionCode().equals(filterRequest.getRegionCode()))
                    .collect(Collectors.toList());
        }
        if (filterRequest.isParking()){
            filteredList =  filteredList.stream()
                    .filter(place -> place.getIsParking().equals("주차가능"))
                    .collect(Collectors.toList());
        }
        if (filterRequest.isInside()){
            filteredList =  filteredList.stream()
                    .filter(place -> place.getIsInside().equals("실내 동반가능"))
                    .collect(Collectors.toList());
        }
        if (filterRequest.isOutside()){
            filteredList =  filteredList.stream()
                    .filter(place -> place.getIsOutside().equals("실외 동반가능"))
                    .collect(Collectors.toList());
        }
        return filteredList;
    }

    public List<Place> findOpenPlaceProcess(List<Place> placeList){
        List<Place> openPlaces = new ArrayList<>();
        for(Place place : placeList){
            if (isOpenCheckProcess(place)){
                openPlaces.add(place);
            }
        }
        return openPlaces;
    }

    public List<PlaceDistResponse> nearbyPlaceProcess(
            List<Place> placeList, CoordinateRequest coordinate){

        List<PlaceDistResponse> distResponses = new ArrayList<>();
        for(Place place : placeList){
            LocationRequest locationRequest = LocationRequest.builder()
                    .startLatitude(coordinate.getLatitude())
                    .startLongitude(coordinate.getLongitude())
                    .endLatitude(place.getLatitude())
                    .endLongitude(place.getLongitude())
                    .build();
            double distance = getDistanceProcess(locationRequest);
            String distanceToString;
            if (distance >= 1){
                distanceToString = String.format("%.1fkm", distance);
            }else {
                distanceToString = String.format("%.1fm", distance * 1000);
            }
            distResponses.add(new PlaceDistResponse(place,distance,distanceToString));
        }
        distResponses.sort(Comparator.comparingDouble(PlaceDistResponse::getDistance));
        return distResponses;
    }

    private boolean isOpenCheckProcess(Place place){

        Map<String,String> businessHours = new HashMap<>();
        String operatingInfo = place.getOperatingInfo();
        String[] operatingInfoArr = operatingInfo.split(",");
        for(String operatingInfoArrStr : operatingInfoArr){
            String[] businessDayOfObject = operatingInfoArrStr.split("-");
            businessHours.put(businessDayOfObject[0],businessDayOfObject[1]);
        }
        String today = getKoreanDayOfWeek();
        LocalTime now = LocalTime.now();
        if (!businessHours.containsKey(today)) {
            return false;
        }
        String hours = businessHours.get(today);
        String[] times = hours.split("~");
        try{
            LocalTime openTime = LocalTime.parse(times[0]);
            int closeHour = Integer.parseInt(times[1].split(":")[0]);
            int closeMinute = Integer.parseInt(times[1].split(":")[1]);

            boolean nextDay = false;
            if (closeHour >= 24) {
                closeHour -= 24;
                nextDay = true;
            }
            LocalTime closeTime = LocalTime.of(closeHour, closeMinute);
            if (!now.isBefore(openTime)) {
                if (!nextDay && !now.isAfter(closeTime)) {
                    return true;
                } else if (nextDay) {
                    return now.isBefore(LocalTime.of(23, 59)) || !now.isAfter(closeTime);
                }
            }
            return false;
        }catch (DateTimeException e){
            log.error("Error during parsing datetime. PlaceId - {} / Hours - {}",place.getId(),hours);
            return false;
        }
    }

    private String getKoreanDayOfWeek(){
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        return switch (dayOfWeek) {
            case MONDAY -> "월";
            case TUESDAY -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY -> "목";
            case FRIDAY -> "금";
            case SATURDAY -> "토";
            case SUNDAY -> "일";
        };
    }

    private double getDistanceProcess(LocationRequest locationRequest) {
        double theta = locationRequest.getEndLongitude() - locationRequest.getStartLongitude();
        double dist = Math.sin(deg2rad(locationRequest.getStartLatitude())) *
                Math.sin(deg2rad(locationRequest.getEndLatitude())) +
                Math.cos(deg2rad(locationRequest.getStartLatitude())) *
                        Math.cos(deg2rad(locationRequest.getEndLatitude())) *
                        Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1609.344;
        return dist / 1000;
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
