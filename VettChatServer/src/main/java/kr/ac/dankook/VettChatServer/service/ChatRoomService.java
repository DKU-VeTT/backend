package kr.ac.dankook.VettChatServer.service;

import kr.ac.dankook.VettChatServer.config.converter.ChatRoomEntityConverter;
import kr.ac.dankook.VettChatServer.config.converter.MemberEntityConverter;
import kr.ac.dankook.VettChatServer.dto.request.CreateChatRoomRequest;
import kr.ac.dankook.VettChatServer.dto.response.ChatRoomResponse;
import kr.ac.dankook.VettChatServer.entity.ChatRoom;
import kr.ac.dankook.VettChatServer.entity.ChatRoomMember;
import kr.ac.dankook.VettChatServer.entity.ChatRoomPin;
import kr.ac.dankook.VettChatServer.entity.Member;
import kr.ac.dankook.VettChatServer.exception.ApiErrorCode;
import kr.ac.dankook.VettChatServer.exception.ApiException;
import kr.ac.dankook.VettChatServer.repository.ChatMessageRepository;
import kr.ac.dankook.VettChatServer.repository.ChatRoomMemberRepository;
import kr.ac.dankook.VettChatServer.repository.ChatRoomPinRepository;
import kr.ac.dankook.VettChatServer.repository.ChatRoomRepository;
import kr.ac.dankook.VettChatServer.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomPinRepository chatRoomPinRepository;
    private final ChatRoomEntityConverter chatRoomEntityConverter;
    private final ChatRedisService chatRedisService;
    private final MemberEntityConverter memberEntityConverter;

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatRoomByKeywordProcess(String keyword){
        List<ChatRoom> chatRooms = chatRoomRepository
                .findByTitleContainingIgnoreCase(keyword);
        return chatRooms.stream().map(chatRoomEntityConverter::convertChatRoomEntity).toList();
    }

    @Transactional
    public void saveLastMessageProcess(Long roomId, String content){
        ChatRoom chatRoom = chatRoomEntityConverter.getChatRoomByRoomId(roomId);
        chatRoom.setLastMessage(content);
        chatRoom.setLastMessageTime(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public ChatRoomResponse createChatRoomProcess(CreateChatRoomRequest request){

        ChatRoom chatRoom = ChatRoom.builder()
                .title(request.getTitle()).build();
        chatRoomRepository.save(chatRoom);
        Member member = memberEntityConverter.getMemberByMemberId(request.getMemberId());
        ChatRoomMember newMember = ChatRoomMember.builder()
                .member(member)
                .chatRoom(chatRoom).build();
        chatRoom.getMembers().add(newMember);
        return chatRoomEntityConverter.convertChatRoomEntity(chatRoom);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getAllChatRoomProcess(){
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
        return convertChatRoomListResponse(chatRoomList);
    }

    private ChatRoomResponse convertChatRoomEntity(ChatRoom chatRoom){
        return ChatRoomResponse.builder()
                .title(chatRoom.getTitle())
                .roomId(EncryptionUtil.encrypt(chatRoom.getId()))
                .createdDateTime(chatRoom.getCreatedDateTime())
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageTime(chatRoom.getLastMessageTime())
                .memberCount(chatRoom.getMembers().size())
                .build();
    }
    private List<ChatRoomResponse> convertChatRoomListResponse(List<ChatRoom> chatRoomList){
        List<ChatRoomResponse> chatRoomResponseList = new ArrayList<>();
        for(ChatRoom chatRoom : chatRoomList){
            chatRoomResponseList.add(convertChatRoomEntity(chatRoom));
        }
        return chatRoomResponseList;
    }
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatRoomByMemberIdProcess(String memberId){
        Member member = memberEntityConverter.getMemberByMemberId(memberId);
        List<ChatRoom> chatRooms = chatRoomMemberRepository.
                findDistinctChatRoomByMember(member);
        return convertChatRoomListResponse(chatRooms);
    }

    @Transactional
    public ChatRoomResponse registerNewMemberToChatRoomProcess(Long roomId, String memberId){

        ChatRoom chatRoom = chatRoomEntityConverter.getChatRoomByRoomId(roomId);
        Member member = memberEntityConverter.getMemberByMemberId(memberId);
        ChatRoomMember newMember = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatRoomMemberRepository.save(newMember);
        return chatRoomEntityConverter.convertChatRoomEntity(chatRoom);
    }
    @Transactional
    public void unregisterMemberToChatRoomProcess(Long roomId,String memberId){

        ChatRoom chatRoom = chatRoomEntityConverter.getChatRoomByRoomId(roomId);
        Member member = memberEntityConverter.getMemberByMemberId(memberId);
        Optional<ChatRoomMember> targetChatRoomMember = chatRoomMemberRepository.
                findByChatRoomAndMember(chatRoom, member);
        if (targetChatRoomMember.isEmpty()){
            throw new ApiException(ApiErrorCode.CHATROOM_MEMBER_NOT_FOUND);
        }
        Optional<ChatRoomPin> targetPin = chatRoomPinRepository
                .findByChatRoomAndMember(chatRoom,member);
        targetPin.ifPresent(chatRoomPinRepository::delete);

        chatRoomMemberRepository.delete(targetChatRoomMember.get());
        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findByChatRoom(chatRoom);

        chatRedisService.deleteUnreadCount(roomId, memberId);

        if (chatRoomMembers.isEmpty()){
            chatMessageRepository
                    .findAllByChatRoomId(EncryptionUtil.encrypt(roomId))
                    .flatMap(chatMessageRepository::delete)
                    .then()
                    .subscribe();
            chatRoomRepository.delete(chatRoom);
        }
    }
}
