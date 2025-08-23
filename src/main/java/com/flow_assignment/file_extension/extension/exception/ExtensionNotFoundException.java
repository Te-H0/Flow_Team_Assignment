package com.flow_assignment.file_extension.extension.exception;

public class ExtensionNotFoundException extends RuntimeException {
    public ExtensionNotFoundException(String message) {
        super(message);
    }
    public ExtensionNotFoundException() {
        super("해당 확장자를 찾을 수 없습니다.");
    }
}
