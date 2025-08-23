package com.flow_assignment.file_extension.extension.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // @Validated 되어있는 에러 처리 (400)
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("요청 값이 올바르지 않습니다.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_PLAIN)
                .body(msg);
    }

    // 400
    @ExceptionHandler({CustomExtensionLimitExceededException.class, ExtensionNameBadRequestException.class})
    public ResponseEntity<String> handleBadRequest(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_PLAIN)
                .body(e.getMessage());
    }

    // 404
    @ExceptionHandler(ExtensionNotFoundException.class)
    public ResponseEntity<String> handleExtensionNotFound(ExtensionNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.TEXT_PLAIN)
                .body(e.getMessage());
    }

    // 409
    @ExceptionHandler({DuplicateExtensionNameException.class, ExtensionStateConflictException.class,
            ConflictWithFixedExtensionException.class})
    public ResponseEntity<String> handleConflictException(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.TEXT_PLAIN)
                .body(e.getMessage());
    }

    // 500
    @ExceptionHandler({ServerErrorException.class})
    public ResponseEntity<String> handleServerError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_PLAIN)
                .body(e.getMessage());
    }


}
