package kr.ac.dankook.VettAuthServer.exception;

import lombok.Getter;

@Getter
public class MailException extends RuntimeException{

    private final ErrorCode errorCode;

    public MailException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

