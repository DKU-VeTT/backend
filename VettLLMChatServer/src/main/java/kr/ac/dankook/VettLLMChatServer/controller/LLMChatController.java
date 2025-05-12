package kr.ac.dankook.VettLLMChatServer.controller;

import kr.ac.dankook.VettLLMChatServer.config.converter.LLMChatEntityConverter;
import kr.ac.dankook.VettLLMChatServer.dto.request.LLMChatRequest;
import kr.ac.dankook.VettLLMChatServer.dto.response.ApiResponse;
import kr.ac.dankook.VettLLMChatServer.dto.response.LLMChatResponse;
import kr.ac.dankook.VettLLMChatServer.entity.LLMChatSection;
import kr.ac.dankook.VettLLMChatServer.exception.ApiErrorCode;
import kr.ac.dankook.VettLLMChatServer.exception.ValidationException;
import kr.ac.dankook.VettLLMChatServer.service.LLMChatService;
import kr.ac.dankook.VettLLMChatServer.util.DecryptId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/llm/chat")
public class LLMChatController {

    private final LLMChatService llmChatService;
    private final LLMChatEntityConverter llmChatEntityConverter;

    @GetMapping("/{chatSectionId}")
    public ResponseEntity<ApiResponse<List<LLMChatResponse>>> getAllChats(
            @PathVariable @DecryptId Long chatSectionId) {
        LLMChatSection llmChatSection = llmChatEntityConverter.getLLMChatSectionBySectionId(chatSectionId);
        return ResponseEntity.ok(new ApiResponse<>(200,
                llmChatService.getAllChatHistoryProcess(llmChatSection)));
    }

    @PostMapping("/{chatSectionId}")
    public ResponseEntity<ApiResponse<LLMChatResponse>> saveNewChat(
            @PathVariable @DecryptId Long chatSectionId,
            @RequestBody LLMChatRequest llmChatRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            validateBindingResult(bindingResult);
        }
        LLMChatSection llmChatSection = llmChatEntityConverter.getLLMChatSectionBySectionId(chatSectionId);
        return ResponseEntity.ok(new ApiResponse<>(200,
                llmChatService.saveLLMChatProcess(llmChatSection, llmChatRequest)));
    }

    private void validateBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(","));
            throw new ValidationException(ApiErrorCode.INVALID_REQUEST,errorMessages);
        }
    }
}
