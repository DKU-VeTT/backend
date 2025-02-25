package kr.ac.dankook.VettAdminServer.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int statusCode;
    private final String message;
    private final boolean success = false;

    public ErrorResponse(ErrorCode errorCode) {
        this.statusCode = errorCode.getHttpStatus().value();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(ErrorCode errorCode,String errorDetails){
        this.statusCode = errorCode.getHttpStatus().value();
        this.message = errorDetails;
    }
}