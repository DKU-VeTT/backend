package kr.ac.dankook.VettAuthServer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode{

    UNAUTHORIZED_ACCESS_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "AccessToken is Required."),
    FORBIDDEN_UNAUTHORIZED_ACTION(HttpStatus.FORBIDDEN, "You are not authorized to perform this action."),
    UNAUTHORIZED_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "There is no member matching the provided username and password."),
    CLIENT_CLOSED_REQUEST_ACCESS_TOKEN_EXPIRED(HttpStatus.valueOf(408), "AccessToken is expired."),
    UNAUTHORIZED_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AccessToken is not valid."),
    INTERNAL_SERVER_ERROR_TOKEN_PARSING(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred during token parsing processing.");

    private final HttpStatus httpStatus;
    private final String message;
}
