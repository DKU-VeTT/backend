package kr.ac.dankook.VettAdminServer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListResponse {

    private List<ChatRoomResponse> chatRoomList;
    private int chatRoomCount;
}
