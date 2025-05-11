package kr.ac.dankook.VettChatServer.repository;

import kr.ac.dankook.VettChatServer.entity.ChatRoom;
import kr.ac.dankook.VettChatServer.entity.ChatRoomPin;
import kr.ac.dankook.VettChatServer.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomPinRepository extends JpaRepository<ChatRoomPin,Long> {
    Optional<ChatRoomPin> findByChatRoomAndMember(ChatRoom chatRoom, Member member);
    List<ChatRoomPin> findByMember(Member member);
}
