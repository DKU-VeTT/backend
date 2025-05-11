package kr.ac.dankook.VettChatServer.service;

import kr.ac.dankook.VettChatServer.config.converter.ChatRoomEntityConverter;
import kr.ac.dankook.VettChatServer.entity.ChatRoom;
import kr.ac.dankook.VettChatServer.entity.ChatRoomMember;
import kr.ac.dankook.VettChatServer.entity.Member;
import kr.ac.dankook.VettChatServer.repository.ChatRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatRedisService {

    private final RedisTemplate<String,Integer> redisTemplate;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRoomEntityConverter chatRoomEntityConverter;

    @SuppressWarnings("ConstantConditions")
    public void clearUnreadCount(Long roomId, String memberId){
        ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
        String key = roomId+"_"+memberId;
        Integer currentUnread = operations.get(key);
        if (currentUnread != null){
            delete(key);
        }
        operations.set(key,0);
    }

    public int getUnreadCount(Long roomId,String memberId){
        ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
        String key = roomId+"_"+memberId;
        return operations.get(key);
    }

    public void deleteUnreadCount(Long roomId, String memberId){
        String key = roomId+"_"+memberId;
        delete(key);
    }

    public void updateAllUnreadCount(Long roomId) {

        ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
        ChatRoom chatRoom = chatRoomEntityConverter
                .getChatRoomByRoomId(roomId);
        List<ChatRoomMember> allMembers = chatRoomMemberRepository.findByChatRoom(chatRoom);

        for(ChatRoomMember chatRoomMember : allMembers){
            Member member = chatRoomMember.getMember();
            String key = roomId+"_"+member.getId();
            operations.increment(key);
        }
    }

    private void delete(String key){
        redisTemplate.delete(key);
    }
}
