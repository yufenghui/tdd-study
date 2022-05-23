package com.yufenghui.tdd.di;

import jakarta.inject.Provider;

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

    public <ComponentType> void bind(Class<ComponentType> type, ComponentType instance) {
        providers.put(type, (Provider<ComponentType>) () -> instance);
    }

    public <ComponentType, ComponentImplementation extends ComponentType> void bind(Class<ComponentType> type, Class<ComponentImplementation> implementation) {
        providers.put(type, (Provider<ComponentType>) () -> {
            try {
                return (ComponentType) implementation.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("cannot find default constructor.", e);
            }
        });
    }

    public <ComponentType> ComponentType get(Class<ComponentType> type) {
        return (ComponentType) providers.get(type).get();
    }

}
