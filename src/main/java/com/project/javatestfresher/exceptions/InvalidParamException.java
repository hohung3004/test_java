package com.project.javatestfresher.exceptions;

import com.project.javatestfresher.enums.ErrorCode;

public class InvalidParamException extends ManagerException {
    public InvalidParamException(String message) {
        super(ErrorCode.valueOf(message));
    }
}
