package kr.ac.dankook.VettChatServer.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberEventMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MemberEvent jsonToEventMemberObject(String json) {
        try{
            return objectMapper.readValue(json, MemberEvent.class);
        }catch (JsonProcessingException e){
            return null;
        }
    }
}
