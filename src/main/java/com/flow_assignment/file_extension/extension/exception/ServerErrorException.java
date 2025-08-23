package com.flow_assignment.file_extension.extension.exception;

public class ServerErrorException extends RuntimeException{
    public ServerErrorException(String message) {
        super(message);
    }

    public ServerErrorException(){
        super("서버 에러입니다.");
    }
}
