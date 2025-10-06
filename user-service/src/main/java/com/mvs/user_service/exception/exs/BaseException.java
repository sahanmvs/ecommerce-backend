package com.mvs.user_service.exception.exs;

import com.mvs.user_service.exception.ExceptionType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BaseException extends RuntimeException{
    private String message;
    private ExceptionType type;
    public BaseException(ExceptionType type, String message) {
        super(message);
        this.type = type;
        this.message = message;
    }

    public HttpStatus getStatus() {return HttpStatus.INTERNAL_SERVER_ERROR;}
}
