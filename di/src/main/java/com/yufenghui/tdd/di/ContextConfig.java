package com.yufenghui.tdd.di;

import com.yufenghui.tdd.di.exception.CyclicDependencyFoundException;
import com.yufenghui.tdd.di.exception.DependencyNotFoundException;
import com.yufenghui.tdd.di.exception.IllegalComponentException;
import jakarta.inject.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Context Config
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/23 13:15
 */
public class ContextConfig {

    private final Map<Class<?>, ComponentProvider<?>> providers = new HashMap<>();
    private final Map<Class<?>, List<Class<?>>> dependencies = new HashMap<>();

    public <Type> void bind(Class<Type> type, Type instance) {
        providers.put(type, context ->  instance);
        dependencies.put(type, List.of());
    }

    public <Type, Implementation extends Type> void bind(Class<Type> type, Class<Implementation> implementation) {
        Constructor<Type> constructor = getConstructor(implementation);
        providers.put(type, new ConstructorInjectProvider<>(type, constructor));
        dependencies.put(type, Arrays.stream(constructor.getParameters()).map(p -> p.getType()).collect(Collectors.toList()));
    }

    public Context getContext() {
        // check dependencies
        dependencies.forEach((component, dependencies) -> {

            dependencies.forEach(dependency -> {
                if(!providers.containsKey(dependency)) {
                    throw new DependencyNotFoundException(component, dependency);
                }
            });

        });


        return new Context() {
            @Override
            public <Type> Optional<Type> get(Class<Type> type) {
                return Optional.ofNullable(providers.get(type)).map(t -> (Type) t.get(this));
            }
        };
    }

    interface ComponentProvider<T> {
        T get(Context context);
    }

    class ConstructorInjectProvider<T> implements ComponentProvider<T> {
        private Class<?> componentType;
        private Constructor<T> constructor;
        private boolean constructing = false;

        public ConstructorInjectProvider(Class<?> componentType, Constructor<T> constructor) {
            this.componentType = componentType;
            this.constructor = constructor;
        }

        @Override
        public T get(Context context) {
            if(constructing) {
                throw new CyclicDependencyFoundException(componentType);
            }

            try {
                constructing = true;
                Object[] args = Arrays.stream(constructor.getParameters())
                        .map(p -> context.get(p.getType())
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
