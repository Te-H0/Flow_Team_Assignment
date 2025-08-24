package com.flow_assignment.file_extension.extension.exception;

public class DuplicateCustomExtensionNameException extends RuntimeException {
    public DuplicateCustomExtensionNameException(String message) {
        super(message);
    }

    public DuplicateCustomExtensionNameException() {
        super("동일한 이름의 확장자가 커스텀 확장자에 이미 존재합니다.");
    }
}
