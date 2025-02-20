package kr.ac.dankook.VettAuthServer.dto.response;

public record ApiMessageResponse(boolean success, int statusCode, String message) { }
