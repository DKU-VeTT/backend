package kr.ac.dankook.VettLLMChatServer.controller;

import kr.ac.dankook.VettLLMChatServer.config.converter.MemberEntityConverter;
import kr.ac.dankook.VettLLMChatServer.dto.response.ApiResponse;
import kr.ac.dankook.VettLLMChatServer.dto.response.LLMChatSectionResponse;
import kr.ac.dankook.VettLLMChatServer.entity.Member;
import kr.ac.dankook.VettLLMChatServer.service.LLMChatSectionService;
import kr.ac.dankook.VettLLMChatServer.util.DecryptId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/llm")
public class LLMChatSectionController {

    private final LLMChatSectionService llmChatSectionService;
    private final MemberEntityConverter memberEntityConverter;

    @PostMapping("/chat-section/{memberId}/{title}")
    public ResponseEntity<ApiResponse<LLMChatSectionResponse>> saveNewChatSection(
            @PathVariable String memberId,
            @PathVariable String title) {
        Member member = memberEntityConverter.getMemberByMemberId(memberId);
        return ResponseEntity.ok(new ApiResponse<>(200,
                llmChatSectionService.saveNewChatSectionProcess(member,title)));
    }

    @DeleteMapping("/chat-section/{chatSectionId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteChatSection(
            @PathVariable @DecryptId Long chatSectionId){
        llmChatSectionService.deleteChatSectionProcess(chatSectionId);
        return ResponseEntity.ok(new ApiResponse<>(200,true));
    }

    @PatchMapping("/chat-section/{chatSectionId}/{title}")
    public ResponseEntity<ApiResponse<Boolean>> updateChatSection(
        @PathVariable @DecryptId Long chatSectionId,
        @PathVariable String title
    ){
        return ResponseEntity.ok(new ApiResponse<>(200,
                llmChatSectionService.updateChatSectionProcess(chatSectionId,title)));
    }

    @GetMapping("/chat-sections/{memberId}")
    public ResponseEntity<ApiResponse<List<LLMChatSectionResponse>>> getAllSectionsByMember(
            @PathVariable String memberId
    ){
        Member member = memberEntityConverter.getMemberByMemberId(memberId);
        return ResponseEntity.ok(new ApiResponse<>(200,
                llmChatSectionService.getAllChatSectionsByMemberProcess(member)));
    }
}
