package com.yufenghui.tdd.di;

import com.yufenghui.tdd.di.exception.IllegalComponentException;
import jakarta.inject.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ConstructorInjectProvider
 *
 * @author yufenghui
 * @date 2022/5/29 20:57
 */
class ConstructorInjectProvider<T> implements ComponentProvider<T> {

    private Constructor<T> constructor;
    private List<Field> fields;

    public ConstructorInjectProvider(Class<T> component) {
        this.constructor = getInjectConstructor(component);
        this.fields = getInjectFields(component);
    }

    private Constructor<T> getInjectConstructor(Class<T> component) {
        List<Constructor<?>> constructorList = Arrays.stream(component.getConstructors())
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());

        if (constructorList.size() > 1) {
            throw new IllegalComponentException("more than one inject constructor exist.");
        }

        return (Constructor<T>) constructorList.stream().findFirst().orElseGet(() -> {
            try {
                return component.getDeclaredConstructor();
            } catch (Exception e) {
                throw new IllegalComponentException("cannot find default constructor.", e);
            }
        });
    }

    private <T> List<Field> getInjectFields(Class<T> component) {
        return Arrays.stream(component.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());
    }

    @Override
    public T get(Context context) {
        try {
            Object[] args = Arrays.stream(constructor.getParameters())
                    .map(p -> context.get(p.getType()).get())
                    .toArray(Object[]::new);
            T instance = constructor.newInstance(args);

            for (Field field : fields) {
                Object dependency = context.get(field.getType()).get();
                field.setAccessible(true);
                field.set(instance, dependency);
            }

            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("bind component failed.", e);
        }
    }

    @Override
    public List<Class<?>> getDependencies() {
        return Stream.concat(Arrays.stream(constructor.getParameters()).map(Parameter::getType),
                Arrays.stream(constructor.getClass().getDeclaredFields()).filter(f -> f.isAnnotationPresent(Inject.class)).map(f -> f.getType())
        ).collect(Collectors.toList());
    }

}
