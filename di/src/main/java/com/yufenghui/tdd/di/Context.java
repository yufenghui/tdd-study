package com.yufenghui.tdd.di;

import com.yufenghui.tdd.di.exception.CyclicDependencyFoundException;
import com.yufenghui.tdd.di.exception.DependencyNotFoundException;
import com.yufenghui.tdd.di.exception.IllegalComponentException;
import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

        providers.put(type, new ConstructorInjectProvider<>(type, constructor));
    }

    public <Type> Optional<Type> get(Class<Type> type) {
        return Optional.ofNullable(providers.get(type)).map(t -> (Type) t.get());
    }

    class ConstructorInjectProvider<T> implements Provider<T> {
        private Class<?> componentType;
        private Constructor<T> constructor;
        private boolean constructing = false;

        public ConstructorInjectProvider(Class<?> componentType, Constructor<T> constructor) {
            this.componentType = componentType;
            this.constructor = constructor;
        }

        @Override
        public T get() {
            if(constructing) {
                throw new CyclicDependencyFoundException(componentType);
            }

            try {
                constructing = true;
                Object[] args = Arrays.stream(constructor.getParameters())
                        .map(p -> Context.this.get(p.getType())
                                .orElseThrow(() -> new DependencyNotFoundException(componentType, p.getType()))
                        )
                        .toArray(Object[]::new);
                return constructor.newInstance(args);
            } catch (CyclicDependencyFoundException e) {
                throw new CyclicDependencyFoundException(componentType, e);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("bind component failed.", e);
            } finally {
                constructing = false;
            }
        }

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

}
