package com.flow_assignment.file_extension.extension.exception;

public class CustomExtensionLimitExceededException extends RuntimeException {
    public CustomExtensionLimitExceededException(String message) {
        super(message);
    }
    public CustomExtensionLimitExceededException(){
        super("최대 커스텀 확장자 수를 초과 했습니다.");
    }
}
