package kr.ac.dankook.VettChatServer.service;


import kr.ac.dankook.VettChatServer.config.converter.ChatRoomEntityConverter;
import kr.ac.dankook.VettChatServer.config.converter.MemberEntityConverter;
import kr.ac.dankook.VettChatServer.entity.ChatRoom;
import kr.ac.dankook.VettChatServer.entity.ChatRoomPin;
import kr.ac.dankook.VettChatServer.entity.Member;
import kr.ac.dankook.VettChatServer.repository.ChatRoomPinRepository;
import kr.ac.dankook.VettChatServer.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomPinService {

    private final ChatRoomPinRepository chatRoomPinRepository;
    private final ChatRoomEntityConverter chatRoomEntityConverter;
    private final MemberEntityConverter memberEntityConverter;

    @Transactional(readOnly = true)
    public String isPinProcess(Long roomId,String memberId){

        Member member = memberEntityConverter.getMemberByMemberId(memberId);
        ChatRoom chatRoom = chatRoomEntityConverter.getChatRoomByRoomId(roomId);

        Optional<ChatRoomPin> targetPin = chatRoomPinRepository
                .findByChatRoomAndMember(chatRoom,member);
        return targetPin.map(chatRoomPin -> EncryptionUtil.encrypt(chatRoomPin.getId())).orElse(null);
    }

    @Transactional
    public String savePinProcess(Long roomId,String memberId){

        Member member = memberEntityConverter.getMemberByMemberId(memberId);
        ChatRoom chatRoom = chatRoomEntityConverter.getChatRoomByRoomId(roomId);

        ChatRoomPin newPin = ChatRoomPin.builder()
                .chatRoom(chatRoom).member(member).build();
        chatRoomPinRepository.save(newPin);
        return EncryptionUtil.encrypt(newPin.getId());
    }

    @Transactional
    public boolean deletePinProcess(Long pinId){
        Optional<ChatRoomPin> targetPin = chatRoomPinRepository.findById(pinId);
        if (targetPin.isPresent()){
            chatRoomPinRepository.deleteById(pinId);
            return true;
        }
        return false;
    }

}
