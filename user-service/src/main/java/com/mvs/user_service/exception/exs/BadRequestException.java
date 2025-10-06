package com.mvs.user_service.exception.exs;

import com.mvs.user_service.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {
    public BadRequestException(ExceptionType type, String message) {
        super(type, message);
    }

    @Override
    public HttpStatus getStatus() {return HttpStatus.BAD_REQUEST;}
}
