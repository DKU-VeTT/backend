package kr.ac.dankook.VettAuthServer.exception;

import lombok.Getter;

@Getter
public class KafkaSendException extends RuntimeException{

    private final ErrorCode errorCode;

    public KafkaSendException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
