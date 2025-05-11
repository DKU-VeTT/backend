package kr.ac.dankook.VettChatServer.repository;

import kr.ac.dankook.VettChatServer.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    List<ChatRoom> findByTitleContainingIgnoreCase(String keyword);
}
