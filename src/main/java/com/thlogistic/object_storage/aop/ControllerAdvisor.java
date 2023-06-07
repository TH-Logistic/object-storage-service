package com.thlogistic.object_storage.aop;

import com.thlogistic.object_storage.adapters.dtos.ErrorResponse;
import com.thlogistic.object_storage.aop.exception.BadRequestException;
import com.thlogistic.object_storage.aop.exception.CustomRuntimeException;
import com.thlogistic.object_storage.aop.exception.DataNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        Map<String, Object> response = ErrorResponse.errorResponse(message, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(
            DataNotFoundException ex, WebRequest request) {
        Map<String, Object> response = ErrorResponse.errorResponse(ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(
            BadRequestException ex, WebRequest request) {
        Map<String, Object> response = ErrorResponse.errorResponse(ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomRuntimeException.class)
    public ResponseEntity<Object> handleCustomRuntimeException(
            CustomRuntimeException ex, WebRequest request) {
        Map<String, Object> response = ErrorResponse.errorResponse(ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

