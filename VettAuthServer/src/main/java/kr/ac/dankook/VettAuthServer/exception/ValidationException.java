package kr.ac.dankook.VettAuthServer.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorDetails;

    public ValidationException(ErrorCode errorCode, String errorDetails) {
        super(errorDetails);
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }
}
