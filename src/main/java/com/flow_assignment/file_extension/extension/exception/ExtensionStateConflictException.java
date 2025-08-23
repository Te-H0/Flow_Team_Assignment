package com.flow_assignment.file_extension.extension.exception;

public class ExtensionStateConflictException extends RuntimeException {

    public ExtensionStateConflictException(String message) {
        super(message);
    }
    public ExtensionStateConflictException() {
        super("요청하신 상태와 현재 상태가 충돌합니다.");
    }
}
