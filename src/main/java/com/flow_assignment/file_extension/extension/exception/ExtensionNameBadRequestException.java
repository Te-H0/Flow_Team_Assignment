package com.flow_assignment.file_extension.extension.exception;

public class ExtensionNameBadRequestException extends RuntimeException {
    public ExtensionNameBadRequestException(String message) {
        super(message);
    }

    public ExtensionNameBadRequestException() {
        super("확장자 이름 형식이 잘못됐습니다.");
    }
}
