package com.faceos.springbootmybatiscode.utils;

/**
 * Result
 * 结果封装类
 *
 * @author lang
 * @date 2019-07-11
 */
public class Result<T> {
    private static final int SUCCESS_CODE = 200;
    private static final String SUCCESS_INFO = "SUCCESS";
    private static final String Fail_INFO = "FAIL";

    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> success(T data, int code, String msg) {
        return new Result<>(data, code, msg);
    }

    public static Result success() {
        return new Result();
    }

    public static Result error(int code) {
        return new Result(code);
    }

    public static <T> Result<T> error(T data, int code) {
        return new Result( data, code);
    }

    private Result(T data) {
        this.code = SUCCESS_CODE;
        this.msg = SUCCESS_INFO;
        this.data = data;
    }

    private Result() {
        this.code = SUCCESS_CODE;
        this.msg = SUCCESS_INFO;
    }

    private Result(int code) {
        this.code = code;
        this.msg = Fail_INFO;
    }

    private Result(T data, int code) {
        this.code = code;
        this.msg = Fail_INFO;
        this.data = data;
    }

    private Result(T data, int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
