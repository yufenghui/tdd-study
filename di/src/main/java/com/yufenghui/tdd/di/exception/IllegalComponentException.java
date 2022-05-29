package com.yufenghui.tdd.di.exception;

/**
 * IllegalComponentException
 *
 * @author yufenghui
 * @date 2022/5/29 11:08
 */
public class IllegalComponentException extends RuntimeException {

    public IllegalComponentException(String message) {
        super(message);
    }

    public IllegalComponentException(String message, Throwable cause) {
        super(message, cause);
    }

}
