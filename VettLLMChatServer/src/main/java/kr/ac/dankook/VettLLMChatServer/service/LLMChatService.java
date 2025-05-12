package kr.ac.dankook.VettLLMChatServer.service;


import kr.ac.dankook.VettLLMChatServer.dto.request.LLMChatRequest;
import kr.ac.dankook.VettLLMChatServer.dto.response.LLMChatResponse;
import kr.ac.dankook.VettLLMChatServer.entity.LLMChat;
import kr.ac.dankook.VettLLMChatServer.entity.LLMChatSection;
import kr.ac.dankook.VettLLMChatServer.repository.LLMChatRepository;
import kr.ac.dankook.VettLLMChatServer.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LLMChatService {

    private final LLMChatRepository llmChatRepository;

    @Transactional
    public List<LLMChatResponse> getAllChatHistoryProcess(LLMChatSection llmChatSection){
        List<LLMChat> llmChats = llmChatRepository.findByLlmChatSection(llmChatSection);
        return llmChats.stream().map(this::convertLLMChatToLLMChatResponse).toList();
    }

    @Transactional
    public LLMChatResponse saveLLMChatProcess(LLMChatSection llmChatSection, LLMChatRequest llmChatRequest){
        LLMChat newLLMChat = LLMChat.builder()
                .llmChatSection(llmChatSection)
                .question(llmChatRequest.getQuestion())
                .answer(llmChatRequest.getAnswer())
                .build();
        LLMChat newInstance = llmChatRepository.save(newLLMChat);
        return convertLLMChatToLLMChatResponse(newInstance);
    }

    private LLMChatResponse convertLLMChatToLLMChatResponse(LLMChat llmChat){

        String encryptChatId = EncryptionUtil.encrypt(llmChat.getId());
        String encryptChatSectionId = EncryptionUtil.encrypt(llmChat.getLlmChatSection().getId());

        return LLMChatResponse.builder()
                .chatId(encryptChatId)
                .chatSectionId(encryptChatSectionId)
                .question(llmChat.getQuestion())
                .answer(llmChat.getAnswer())
                .time(llmChat.getCreatedDateTime())
                .build();
    }

}
