package com.example.demo.config.exception;

/**
 * @author ljr
 * @date 2022-08-29 16:56
 */
public class ResultCode {

    private String msg;
    private int code;
    private Object object;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String mag) {
        this.msg = mag;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
