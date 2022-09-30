package com.example.demo.config.exception;


import lombok.Getter;

/**
 * @author ljr
 * @date 2022-08-29 13:52
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description=description;
    }

    public BusinessException(ResultCode resultCode, String description) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
        this.description=description;
    }
}
