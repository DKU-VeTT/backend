package kr.ac.dankook.VettLLMChatServer.service;

import kr.ac.dankook.VettLLMChatServer.document.ChatHistory;
import kr.ac.dankook.VettLLMChatServer.dto.response.LLMChatSectionResponse;
import kr.ac.dankook.VettLLMChatServer.entity.LLMChatSection;
import kr.ac.dankook.VettLLMChatServer.entity.Member;
import kr.ac.dankook.VettLLMChatServer.repository.ChatHistoryRepository;
import kr.ac.dankook.VettLLMChatServer.repository.LLMChatSectionRepository;
import kr.ac.dankook.VettLLMChatServer.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LLMChatSectionService {

    private final LLMChatSectionRepository llmChatSectionRepository;
    private final ChatHistoryRepository chatHistoryRepository;

    @Transactional
    public LLMChatSectionResponse saveNewChatSectionProcess(Member member, String title){
        LLMChatSection llmChatSection = LLMChatSection
                .builder().member(member).title(title).build();
        LLMChatSection newSection = llmChatSectionRepository.save(llmChatSection);
        return convertToLLMChatSectionResponse(newSection);
    }

    @Transactional(readOnly = true)
    public List<LLMChatSectionResponse> getAllChatSectionsByMemberProcess(Member member){
        List<LLMChatSection> llmChatSections = llmChatSectionRepository.findByMember(member);
        return llmChatSections.stream().map(this::convertToLLMChatSectionResponse).toList();
    }
    @Transactional
    public boolean updateChatSectionProcess(Long sectionId,String newTitle){
        Optional<LLMChatSection> targetSection =
                llmChatSectionRepository.findById(sectionId);
        if (targetSection.isPresent()) {
            targetSection.get().setTitle(newTitle);
            llmChatSectionRepository.save(targetSection.get());
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteChatSectionProcess(Long sectionId){
        List<ChatHistory> historyData = chatHistoryRepository.
                findBySessionId(EncryptionUtil.encrypt(sectionId));
        chatHistoryRepository.deleteAll(historyData);
        llmChatSectionRepository.deleteById(sectionId);
    }

    private LLMChatSectionResponse convertToLLMChatSectionResponse(LLMChatSection llmChatSection){

        return LLMChatSectionResponse
                .builder().id(EncryptionUtil.encrypt(llmChatSection.getId()))
                .title(llmChatSection.getTitle())
                .time(llmChatSection.getCreatedDateTime()).build();
    }
}
