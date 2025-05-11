package kr.ac.dankook.VettChatServer.repository;

import kr.ac.dankook.VettChatServer.entity.ChatRoom;
import kr.ac.dankook.VettChatServer.entity.ChatRoomMember;
import kr.ac.dankook.VettChatServer.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    @Query("SELECT DISTINCT c.chatRoom FROM ChatRoomMember c WHERE c.member = :member")
    List<ChatRoom> findDistinctChatRoomByMember(@Param("member") Member member);
    Optional<ChatRoomMember> findByChatRoomAndMember(ChatRoom chatRoom, Member member);
    List<ChatRoomMember> findByChatRoom(ChatRoom chatRoom);
    List<ChatRoomMember> findByMember(Member member);
}
