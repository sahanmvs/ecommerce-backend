package com.mvs.user_service.exception.exs;

import com.mvs.user_service.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException{
    public ConflictException(ExceptionType type, String message) {
        super(type, message);
    }

    @Override
    public HttpStatus getStatus() {return HttpStatus.CONFLICT;}
}
