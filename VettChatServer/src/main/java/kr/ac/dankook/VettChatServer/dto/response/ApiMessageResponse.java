package kr.ac.dankook.VettChatServer.dto.response;

public record ApiMessageResponse(boolean success, int statusCode, String message) { }
