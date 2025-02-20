package kr.ac.dankook.VettAuthServer.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettAuthServer.exception.ApiErrorCode;
import kr.ac.dankook.VettAuthServer.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventObjectMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String eventObjectToJson(Object eventObject) {

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(eventObject);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON - {}",eventObject);
            throw new ApiException(ApiErrorCode.JSON_CONVERT_ERROR);
        }
        return payloadJson;
    }

    public MemberEvent jsonToEventObject(String json) {
       try{
           return objectMapper.readValue(json, MemberEvent.class);
       }catch (JsonProcessingException e){
           log.error("Failed to convert Json to Object - {}",json);
           throw new ApiException(ApiErrorCode.JSON_CONVERT_ERROR);
       }
    }
}
