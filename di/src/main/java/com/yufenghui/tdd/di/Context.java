package com.yufenghui.tdd.di;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
        providers.put(type, (Provider<Type>) () -> {
            try {
                Constructor<Type> constructor = getInjectConstructor(implementation);
                Object[] args = Arrays.stream(constructor.getParameters()).map(p -> get(p.getType())).toArray(Object[]::new);

                return constructor.newInstance(args);
            } catch (Exception e) {
                throw new RuntimeException("new instance with constructor failed.", e);
            }
        });
    }

    private <Type, Implementation extends Type> Constructor<Type> getInjectConstructor(Class<Implementation> implementation) {

        Constructor<Type> constructor = (Constructor<Type>) Arrays.stream(implementation.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class))
                .findFirst().orElseGet(() -> {
                    try {
                        return implementation.getConstructor();
                    } catch (Exception e) {
                        throw new RuntimeException("cannot find constructor.", e);
                    }
                });

        return constructor;
    }

    public <Type> Type get(Class<Type> type) {
        return (Type) providers.get(type).get();
    }

}
