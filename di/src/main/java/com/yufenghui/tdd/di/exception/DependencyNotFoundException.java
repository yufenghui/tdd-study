package com.yufenghui.tdd.di.exception;

/**
 * DependencyNotFoundException
 *
 * @author yufenghui
 * @date 2022/5/29 12:30
 */
public class DependencyNotFoundException extends RuntimeException {

    private Class<?> component;
    private Class<?> dependency;

    public DependencyNotFoundException(Class<?> dependency) {
        this.dependency = dependency;
    }

    public DependencyNotFoundException(Class<?> component, Class<?> dependency) {
        this.component = component;
        this.dependency = dependency;
    }

    public Class<?> getDependency() {
        return this.dependency;
    }

    public Class<?> getComponent() {
        return this.component;
    }
}
