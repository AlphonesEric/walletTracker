package com.alphones.web;

import java.io.Serializable;

public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int SUCCESS = HttpStatus.SUCCESS;
    public static final int FAIL = HttpStatus.ERROR;
    private int code;
    private String msg;
    private T data;

    public static <T> R<T> ok() {
        return restResult(null, HttpStatus.SUCCESS, "操作成功");
    }

    public static <T> R<T> ok(final T data) {
        return restResult(data, HttpStatus.SUCCESS, "操作成功");
    }

    public static <T> R<T> ok(final T data, final String msg) {
        return restResult(data, HttpStatus.SUCCESS, msg);
    }

    public static <T> R<T> fail() {
        return restResult(null, HttpStatus.ERROR, "操作失败");
    }

    public static <T> R<T> fail(final String msg) {
        return restResult(null, HttpStatus.ERROR, msg);
    }

    public static <T> R<T> fail(final T data) {
        return restResult(data, HttpStatus.ERROR, "操作失败");
    }

    public static <T> R<T> fail(final T data, final String msg) {
        return restResult(data, HttpStatus.ERROR, msg);
    }

    public static <T> R<T> fail(final int code, final String msg) {
        return restResult(null, code, msg);
    }

    private static <T> R<T> restResult(final T data, final int code, final String msg) {
        final R<T> apiResult = new R<T>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public T getData() {
        return this.data;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public static <T> Boolean isError(final R<T> ret) {
        return !isSuccess(ret);
    }

    public static <T> Boolean isSuccess(final R<T> ret) {
        return HttpStatus.SUCCESS == ret.getCode();
    }
}
