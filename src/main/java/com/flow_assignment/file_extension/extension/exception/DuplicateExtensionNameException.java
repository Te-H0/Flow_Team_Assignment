package com.flow_assignment.file_extension.extension.exception;

public class DuplicateExtensionNameException extends RuntimeException{
    public DuplicateExtensionNameException(String message) {
        super(message);
    }
    public DuplicateExtensionNameException() {
        super("이미 존재하는 확장자 입니다.");
    }
}
