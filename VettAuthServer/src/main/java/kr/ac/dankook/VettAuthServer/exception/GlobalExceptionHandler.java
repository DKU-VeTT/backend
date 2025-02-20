package kr.ac.dankook.VettAuthServer.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    public GlobalExceptionHandler() {}

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return handleExceptionInternal(errorCode);
    }
    @ExceptionHandler(EncryptException.class)
    protected ResponseEntity<ErrorResponse> handleEncryptException(EncryptException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return handleExceptionInternal(errorCode);
    }
    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return handleExceptionInternal(errorCode,ex.getErrorDetails());
    }
    @ExceptionHandler(KafkaSendException.class)
    protected ResponseEntity<ErrorResponse> handleKafkaSendException(KafkaSendException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return handleExceptionInternal(errorCode);
    }
    @ExceptionHandler(MailException.class)
    protected ResponseEntity<ErrorResponse> handleMailException(MailException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return handleExceptionInternal(errorCode);
    }
    private ResponseEntity<ErrorResponse> handleExceptionInternal(ErrorCode errorCode){
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode));
    }
    private ResponseEntity<ErrorResponse> handleExceptionInternal(ErrorCode errorCode,String errorDetails){
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ErrorResponse(errorCode,errorDetails));
    }
}
