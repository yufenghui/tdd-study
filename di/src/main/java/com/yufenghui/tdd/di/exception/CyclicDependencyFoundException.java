package com.yufenghui.tdd.di.exception;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CyclicDependencyFoundException
 *
 * @author yufenghui
 * @date 2022/5/29 13:11
 */
public class CyclicDependencyFoundException extends RuntimeException {

    private Set<Class<?>> components = new HashSet<>();

    public CyclicDependencyFoundException(List<Class<?>> visiting) {
        components.addAll(visiting);
    }

    public Class<?>[] getComponents() {
        return this.components.toArray(Class<?>[]::new);
    }

}
