package kr.ac.dankook.VettChatServer.controller;

import jakarta.validation.Valid;
import kr.ac.dankook.VettChatServer.dto.request.CreateChatRoomRequest;
import kr.ac.dankook.VettChatServer.dto.response.ApiMessageResponse;
import kr.ac.dankook.VettChatServer.dto.response.ApiResponse;
import kr.ac.dankook.VettChatServer.dto.response.ChatRoomListResponse;
import kr.ac.dankook.VettChatServer.dto.response.ChatRoomResponse;
import kr.ac.dankook.VettChatServer.exception.ApiErrorCode;
import kr.ac.dankook.VettChatServer.exception.ValidationException;
import kr.ac.dankook.VettChatServer.service.ChatRoomService;
import kr.ac.dankook.VettChatServer.util.DecryptId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/rooms/keyword/{keyword}")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getChatRoomByKeyword
            (@PathVariable("keyword") String keyword) {
        return ResponseEntity.ok(new ApiResponse<>(200,
                chatRoomService.getChatRoomByKeywordProcess(keyword)));
    }

    @GetMapping("/room/is-participate/{roomId}/{memberId}")
    public ResponseEntity<ApiResponse<Boolean>> isParticipateChatRoom(
            @PathVariable("roomId") @DecryptId Long roomId,
            @PathVariable("memberId") String memberId
    ){
        return ResponseEntity.ok(new ApiResponse<>(200,
                chatRoomService.isParticipateChatRoomProcess(roomId,memberId)));
    }

    @PostMapping("/room")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createChatRoom(
            @RequestBody @Valid CreateChatRoomRequest createChatRoomRequest,
            BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        return ResponseEntity.ok(new ApiResponse<>(201,
                chatRoomService.createChatRoomProcess(createChatRoomRequest)));
    }
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoomListResponse>> getAllChatRoomList(){
        List<ChatRoomResponse> chatRoomList = chatRoomService.getAllChatRoomProcess();
        return ResponseEntity.ok(new ApiResponse<>(200,new ChatRoomListResponse(chatRoomList,chatRoomList.size())));
    }
    @GetMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<ChatRoomListResponse>> getChatRoomListByMemberId(
            @PathVariable String id){
        List<ChatRoomResponse> chatRoomList = chatRoomService.getChatRoomByMemberIdProcess(id);
        return ResponseEntity.ok(new ApiResponse<>(200,new ChatRoomListResponse(chatRoomList,chatRoomList.size())));
    }
    @PostMapping("/room/{roomId}/{memberId}")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> registerNewMemberToChatRoom(
            @PathVariable @DecryptId Long roomId,
            @PathVariable String memberId
    ) {
        return ResponseEntity.ok(new ApiResponse<>(201,
                chatRoomService.registerNewMemberToChatRoomProcess(roomId, memberId)));
    }

    @DeleteMapping("/room/{roomId}/{memberId}")
    public ResponseEntity<ApiMessageResponse> unregisterToChatRoom(
            @PathVariable @DecryptId Long roomId,
            @PathVariable String memberId
    ){
        chatRoomService.unregisterMemberToChatRoomProcess(roomId,memberId);
        return ResponseEntity.ok(new ApiMessageResponse(true, 200, "Success unRegister ChatRoom"));
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
