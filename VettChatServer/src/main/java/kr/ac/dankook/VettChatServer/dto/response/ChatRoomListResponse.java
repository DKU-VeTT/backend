package kr.ac.dankook.VettChatServer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListResponse {

    private List<ChatRoomResponse> chatRoomList;
    private int chatRoomCount;
}
