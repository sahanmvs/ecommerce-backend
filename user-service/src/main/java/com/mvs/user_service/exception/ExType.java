package com.mvs.user_service.exception;

public enum ExType implements ExceptionType {
    UNAUTHORIZED, INVALID_CREDENTIALS, USER_ALREADY_EXISTS, USER_NOT_FOUND;

    @Override
    public String getExceptionType() {
        return this.toString();
    }
}
