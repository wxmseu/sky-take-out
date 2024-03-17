package com.sky.exception;

/**
 * 用户名重复
 */
public class UsernameDuplicateException extends BaseException {


    public UsernameDuplicateException() {
    }

    public UsernameDuplicateException(String msg) {
        super(msg);
    }
}
