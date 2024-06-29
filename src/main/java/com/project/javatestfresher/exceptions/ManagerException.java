package com.project.salebe.exceptions;

import com.project.salebe.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class SaleappException extends RuntimeException {
    private ErrorCode errorCode;
    private HttpStatus httpStatus;

    public SaleappException(ErrorCode errorCode, String... args) {
        super(errorCode.format(args));
        this.errorCode = errorCode;
    }

    public SaleappException(HttpStatus httpStatus, ErrorCode errorCode, String... args) {
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

