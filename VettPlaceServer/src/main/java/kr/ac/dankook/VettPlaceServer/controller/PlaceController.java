package kr.ac.dankook.VettPlaceServer.controller;

import jakarta.validation.Valid;
import kr.ac.dankook.VettPlaceServer.document.Place;
import kr.ac.dankook.VettPlaceServer.dto.request.CoordinateRequest;
import kr.ac.dankook.VettPlaceServer.dto.request.FilterRequest;
import kr.ac.dankook.VettPlaceServer.dto.response.ApiResponse;
import kr.ac.dankook.VettPlaceServer.dto.response.PlaceDistResponse;
import kr.ac.dankook.VettPlaceServer.dto.response.PlaceResponse;
import kr.ac.dankook.VettPlaceServer.exception.ApiErrorCode;
import kr.ac.dankook.VettPlaceServer.exception.ValidationException;
import kr.ac.dankook.VettPlaceServer.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;

    // 모든 장소 데이터
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PlaceResponse>> findAllPlaces(){
        List<Place> places = placeService.findAllPlaceProcess();
        return ResponseEntity.ok(new ApiResponse<>(200, new PlaceResponse(places,places.size())));
    }

    // 카테고리 별 장소 데이터
    @GetMapping("/category")
    public ResponseEntity<ApiResponse<PlaceResponse>> findAllByCategories(
            @RequestParam String category){
        List<Place> places = placeService.findPlaceByCategoryProcess(category);
        return ResponseEntity.ok(new ApiResponse<>(200, new PlaceResponse(places,places.size())));
    }

    // 장소 카테고리 종류
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories(){
        return ResponseEntity.ok(new ApiResponse<>(200, placeService.findPlaceCategory()));
    }

    // 거리 순으로 데이터 정렬 ( 좌표 입력, 카테고리 입력 )
    @PostMapping("/dist/{category}")
    public ResponseEntity<ApiResponse<List<PlaceDistResponse>>> getPlaceByDist(
            @RequestBody @Valid CoordinateRequest coordinateRequest, BindingResult bindingResult,
            @PathVariable String category){
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        List<Place> categoryPlaces = placeService.findPlaceByCategoryProcess(category);
        List<PlaceDistResponse> distPlaces = placeService.nearbyPlaceProcess(categoryPlaces,coordinateRequest);
        return ResponseEntity.ok(new ApiResponse<>(200,distPlaces));
    }

    // 현재 운영중인 장소 리스트, 카테고리 검색 필요
    @GetMapping("/open/{category}")
    public ResponseEntity<ApiResponse<PlaceResponse>> getPlaceByIsOpen(
            @PathVariable String category){
        List<Place> categoryPlaces = placeService.findPlaceByCategoryProcess(category);
        List<Place> openPlaces = placeService.findOpenPlaceProcess(categoryPlaces);
        return ResponseEntity.ok(new ApiResponse<>(200,new PlaceResponse(openPlaces,openPlaces.size())));
    }

    // 현재 운영중이고 거리순으로 데이터를 정렬 ( 좌표 입력, 카테고리 입력 )
    @PostMapping("/open/dist/{category}")
    public ResponseEntity<ApiResponse<List<PlaceDistResponse>>> getPlaceByIsOpenAndDist(
            @RequestBody @Valid CoordinateRequest coordinateRequest,
            BindingResult bindingResult,
            @PathVariable String category
    ){
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        List<Place> categoryPlaces = placeService.findPlaceByCategoryProcess(category);
        List<Place> openPlaces = placeService.findOpenPlaceProcess(categoryPlaces);
        List<PlaceDistResponse> distPlaces = placeService.nearbyPlaceProcess(openPlaces,coordinateRequest);
        return ResponseEntity.ok(new ApiResponse<>(200,distPlaces));
    }

    // 장소 이름으로 키워드 검색
    @GetMapping("/search/{keyword}")
    public ResponseEntity<ApiResponse<PlaceResponse>> searchByKeyword(
            @PathVariable String keyword){
        List<Place> keywordPlaces = placeService.findByKeywordProcess(keyword);
        return ResponseEntity.ok(new ApiResponse<>(200,new PlaceResponse(keywordPlaces,keywordPlaces.size())));
    }

    // 필터를 통해 검색 - 카테고리는 필수 사항
    @PostMapping("/filter")
    public ResponseEntity<ApiResponse<PlaceResponse>> searchByFilterPlace(
            @RequestBody @Valid FilterRequest filterRequest,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        List<Place> filterPlaces = placeService.findPlaceByFilter(filterRequest);
        return ResponseEntity.ok(new ApiResponse<>(200,new PlaceResponse(filterPlaces,filterPlaces.size())));
    }

    private void validateBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(","));
            throw new ValidationException(ApiErrorCode.INVALID_REQUEST,errorMessages);
        }
    }
}
