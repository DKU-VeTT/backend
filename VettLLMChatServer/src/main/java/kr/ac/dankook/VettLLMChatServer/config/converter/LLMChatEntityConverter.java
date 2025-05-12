package kr.ac.dankook.VettLLMChatServer.config.converter;

import kr.ac.dankook.VettLLMChatServer.entity.LLMChatSection;
import kr.ac.dankook.VettLLMChatServer.exception.ApiErrorCode;
import kr.ac.dankook.VettLLMChatServer.exception.ApiException;
import kr.ac.dankook.VettLLMChatServer.repository.LLMChatSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LLMChatEntityConverter {

    private final LLMChatSectionRepository llmChatSectionRepository;

    private LLMChatSection getChatSectionRepository(Long sectionId){
        Optional<LLMChatSection> target = llmChatSectionRepository.findById(sectionId);
        if(target.isPresent()){
            return target.get();
        }
        throw new ApiException(ApiErrorCode.CHAT_SECTION_NOT_FOUND);
    }

    @Transactional(readOnly = true)
    public LLMChatSection getLLMChatSectionBySectionId(Long sectionId){
        return getChatSectionRepository(sectionId);
    }

}
