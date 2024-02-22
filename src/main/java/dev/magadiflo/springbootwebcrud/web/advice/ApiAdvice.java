package dev.magadiflo.springbootwebcrud.web.advice;

import dev.magadiflo.springbootwebcrud.exception.ApiException;
import dev.magadiflo.springbootwebcrud.web.util.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiAdvice {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseMessage<Void>> apiException(ApiException apiException) {
        ResponseMessage<Void> responseMessage = new ResponseMessage<>(apiException.getMessage(), null);
        return ResponseEntity.status(apiException.getHttpStatus()).body(responseMessage);
    }
}
