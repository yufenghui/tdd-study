package com.yufenghui.tdd.di;

import com.yufenghui.tdd.di.exception.DependencyNotFoundException;
import com.yufenghui.tdd.di.exception.IllegalComponentException;
import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Context
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/23 13:15
 */
public class Context {

    private final Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <Type> void bind(Class<Type> type, Type instance) {
        providers.put(type, (Provider<Type>) () -> instance);
    }

    public <Type, Implementation extends Type> void bind(Class<Type> type, Class<Implementation> implementation) {
        Constructor<Type> constructor = getConstructor(implementation);

        providers.put(type, (Provider<Type>) () -> {
            Object[] args = Arrays.stream(constructor.getParameters())
                    .map(p -> get(p.getType()).orElseThrow(() -> new DependencyNotFoundException("dependency not found.")))
                    .toArray(Object[]::new);

            try {
                return constructor.newInstance(args);
            } catch (Exception e) {
                throw new RuntimeException("bind component failed.", e);
            }
        });
    }

    private <Type, Implementation extends Type> Constructor<Type> getConstructor(Class<Implementation> implementation) {
        List<Constructor<?>> constructorList = Arrays.stream(implementation.getConstructors())
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());

        if (constructorList.size() > 1) {
            throw new IllegalComponentException("more than one inject constructor exist.");
        }

        return (Constructor<Type>) constructorList.stream().findFirst().orElseGet(() -> {
                    try {
                        return implementation.getConstructor();
                    } catch (Exception e) {
                        throw new IllegalComponentException("cannot find default constructor.", e);
                    }
                });
    }

    public <Type> Optional<Type> get(Class<Type> type) {
        return Optional.ofNullable(providers.get(type)).map(t -> (Type) t.get());
    }

}
