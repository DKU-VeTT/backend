package kr.ac.dankook.VettChatServer.controller;

import kr.ac.dankook.VettChatServer.dto.response.ApiResponse;
import kr.ac.dankook.VettChatServer.dto.response.ChatRoomListResponse;
import kr.ac.dankook.VettChatServer.dto.response.ChatRoomResponse;
import kr.ac.dankook.VettChatServer.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/chat")
@RequiredArgsConstructor
public class ChatAdminController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoomListResponse>> getAllChatRooms(){
        List<ChatRoomResponse> chatRoomList = chatRoomService.getAllChatRoomProcess();
        return ResponseEntity.ok(new ApiResponse<>(200,new ChatRoomListResponse(chatRoomList,chatRoomList.size())));
    }
}
