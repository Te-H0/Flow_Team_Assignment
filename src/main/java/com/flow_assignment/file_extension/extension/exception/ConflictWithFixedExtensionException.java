package com.flow_assignment.file_extension.extension.exception;

public class ConflictWithFixedExtensionException extends RuntimeException {
    public ConflictWithFixedExtensionException(String message) {
        super(message);
    }

    public ConflictWithFixedExtensionException() {
        super("동일한 이름의 확장자가 고정 확장자에 이미 존재합니다.");
    }
}
