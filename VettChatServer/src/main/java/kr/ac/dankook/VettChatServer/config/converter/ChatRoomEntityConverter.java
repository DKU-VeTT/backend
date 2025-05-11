package kr.ac.dankook.VettChatServer.config.converter;

import kr.ac.dankook.VettChatServer.dto.response.ChatRoomResponse;
import kr.ac.dankook.VettChatServer.entity.ChatRoom;
import kr.ac.dankook.VettChatServer.exception.ApiErrorCode;
import kr.ac.dankook.VettChatServer.exception.ApiException;
import kr.ac.dankook.VettChatServer.repository.ChatRoomRepository;
import kr.ac.dankook.VettChatServer.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChatRoomEntityConverter {

    private final ChatRoomRepository chatRoomRepository;

    private ChatRoom getChatRoomFromRepository(Long roomId){
        Optional<ChatRoom> targetRoom = chatRoomRepository.findById(roomId);
        if(targetRoom.isPresent()){
            return targetRoom.get();
        }
        throw new ApiException(ApiErrorCode.CHATROOM_NOT_FOUND);
    }

    @Transactional(readOnly = true)
    public ChatRoom getChatRoomByRoomId(Long roomId){
        return getChatRoomFromRepository(roomId);
    }

    public ChatRoomResponse convertChatRoomEntity(ChatRoom chatRoom){
        return ChatRoomResponse.builder()
                .title(chatRoom.getTitle())
                .roomId(EncryptionUtil.encrypt(chatRoom.getId()))
                .createdDateTime(chatRoom.getCreatedDateTime())
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageTime(chatRoom.getLastMessageTime())
                .memberCount(chatRoom.getMembers().size())
                .build();
    }
}
