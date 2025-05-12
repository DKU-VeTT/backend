package kr.ac.dankook.VettLLMChatServer.dto.response;

public record ApiMessageResponse(boolean success, int statusCode, String message) { }
