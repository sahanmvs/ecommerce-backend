package com.mvs.user_service.exception.exs;

import com.mvs.user_service.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException{
    public NotFoundException(ExceptionType type, String message) {
        super(type, message);
    }

    @Override
    public HttpStatus getStatus() {return HttpStatus.NOT_FOUND;}
}
