package kr.ac.dankook.VettAdminServer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiErrorCode implements ErrorCode{

    INVALID_OLD_PASSWORD(HttpStatus.UNAUTHORIZED,"Not Equals your old password"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request parameters.");
    private final HttpStatus httpStatus;
    private final String message;
}
