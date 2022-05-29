package com.yufenghui.tdd.di.exception;

/**
 * DependencyNotFoundException
 *
 * @author yufenghui
 * @date 2022/5/29 12:30
 */
public class DependencyNotFoundException extends RuntimeException {

    public DependencyNotFoundException(String message) {
        super(message);
    }

}
