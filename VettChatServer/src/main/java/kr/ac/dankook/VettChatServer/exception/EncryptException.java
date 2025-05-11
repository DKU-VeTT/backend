package kr.ac.dankook.VettChatServer.exception;

import lombok.Getter;

@Getter
public class EncryptException extends RuntimeException{

    private final ErrorCode errorCode;

    public EncryptException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
