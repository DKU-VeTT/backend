package kr.ac.dankook.VettDiagnosisServer.dto.response;

public record ApiMessageResponse(boolean success, int statusCode, String message) { }
