package kr.ac.dankook.VettDiagnosisServer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiErrorCode implements ErrorCode{

    KAFKA_SEND_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"Error during sending data to Kafka"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request parameters."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Not found member with your primary key."),
    DECRYPT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error during decrypting primary key."),
    JSON_CONVERT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to convert Json with Object");

    private final HttpStatus httpStatus;
    private final String message;
}
