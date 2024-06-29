package com.project.javatestfresher.exceptions;

import com.project.javatestfresher.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ManagerException extends RuntimeException {
    private ErrorCode errorCode;
    private HttpStatus httpStatus;

    public ManagerException(ErrorCode errorCode, String... args) {
        super(errorCode.format(args));
        this.errorCode = errorCode;
    }

    public ManagerException(HttpStatus httpStatus, ErrorCode errorCode, String... args) {
        super(errorCode.format(args));
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

//    public ErrorResponse toErrorResponse() {
//        return new ErrorResponse(this.errorCode.name(), this.getMessage());
//    }
}

